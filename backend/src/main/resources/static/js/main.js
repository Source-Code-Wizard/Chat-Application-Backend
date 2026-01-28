'use strict';

// ==========================================
// DOM Elements
// ==========================================
const loginPage = document.getElementById('login-page');
const chatPage = document.getElementById('chat-page');
const loginForm = document.getElementById('loginForm');
const userIdInput = document.getElementById('userId');
const usernameInput = document.getElementById('username');
const demoBtns = document.querySelectorAll('.demo-btn');

const currentUserName = document.getElementById('currentUserName');
const currentUserAvatar = document.getElementById('currentUserAvatar');
const chatList = document.getElementById('chatList');
const usersList = document.getElementById('usersList');
const newChatBtn = document.getElementById('newChatBtn');
const logoutBtn = document.getElementById('logoutBtn');

const noChatSelected = document.getElementById('noChatSelected');
const chatArea = document.getElementById('chatArea');
const chatName = document.getElementById('chatName');
const chatAvatar = document.getElementById('chatAvatar');
const chatStatus = document.getElementById('chatStatus');
const messagesContainer = document.getElementById('messagesContainer');
const messageForm = document.getElementById('messageForm');
const messageInput = document.getElementById('messageInput');

const newChatModal = document.getElementById('newChatModal');
const closeModalBtn = document.getElementById('closeModalBtn');
const newChatForm = document.getElementById('newChatForm');
const chatIdInput = document.getElementById('chatIdInput');
const chatNameInput = document.getElementById('chatNameInput');
const generateChatIdBtn = document.getElementById('generateChatIdBtn');

const connectionStatus = document.getElementById('connectionStatus');
const statusText = connectionStatus.querySelector('.status-text');

// ==========================================
// State
// ==========================================
let stompClient = null;
let currentUser = {
    id: null,
    username: null
};
let selectedChatId = null;
let chats = new Map(); // chatId -> { name, messages, subscription }
let subscribedChats = new Set();

// ==========================================
// Utility Functions
// ==========================================
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function formatTime(date) {
    return new Date(date).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
}

function getInitials(name) {
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
}

function showConnectionStatus(message, type = 'connecting') {
    statusText.textContent = message;
    connectionStatus.className = 'connection-status visible';
    if (type === 'connected') {
        connectionStatus.classList.add('connected');
    } else if (type === 'error') {
        connectionStatus.classList.add('error');
    }

    if (type === 'connected') {
        setTimeout(() => {
            connectionStatus.classList.remove('visible');
        }, 3000);
    }
}

// ==========================================
// WebSocket Connection
// ==========================================
function connect() {
    showConnectionStatus('Connecting...', 'connecting');

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // Disable debug logging (comment out for debugging)
    stompClient.debug = null;

    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    console.log('WebSocket Connected');
    showConnectionStatus('Connected', 'connected');

    // Subscribe to user-specific notifications (optional)
    stompClient.subscribe(`/user/${currentUser.id}/queue/notifications`, onNotificationReceived);

    // Re-subscribe to any existing chats
    chats.forEach((chat, chatId) => {
        subscribeToChat(chatId);
    });
}

function onError(error) {
    console.error('WebSocket Error:', error);
    showConnectionStatus('Connection failed. Retrying...', 'error');

    // Retry connection after 5 seconds
    setTimeout(() => {
        connect();
    }, 5000);
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log('Disconnected');
}

// ==========================================
// Chat Subscription
// ==========================================
function subscribeToChat(chatId) {
    if (subscribedChats.has(chatId)) {
        console.log(`Already subscribed to chat: ${chatId}`);
        return;
    }

    const subscription = stompClient.subscribe(`/topic/chat/${chatId}`, onMessageReceived);
    subscribedChats.add(chatId);

    // Store subscription reference
    if (chats.has(chatId)) {
        chats.get(chatId).subscription = subscription;
    }

    console.log(`Subscribed to chat: ${chatId}`);
}

function unsubscribeFromChat(chatId) {
    const chat = chats.get(chatId);
    if (chat && chat.subscription) {
        chat.subscription.unsubscribe();
        subscribedChats.delete(chatId);
        console.log(`Unsubscribed from chat: ${chatId}`);
    }
}

// ==========================================
// Message Handling
// ==========================================
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    console.log('Message received:', message);

    // Add message to chat
    const chatId = message.chatId;
    if (chats.has(chatId)) {
        const chat = chats.get(chatId);
        chat.messages.push(message);
        chat.lastMessage = message.content;
        chat.lastMessageTime = message.createdAt || new Date().toISOString();

        // Update chat list UI
        updateChatListItem(chatId);
    }

    // If this chat is currently selected, display the message
    if (selectedChatId === chatId) {
        displayMessage(message);
        scrollToBottom();
    } else {
        // Show unread indicator
        markChatAsUnread(chatId);
    }
}

function onNotificationReceived(payload) {
    const notification = JSON.parse(payload.body);
    console.log('Notification received:', notification);
    // Handle notifications (friend requests, etc.)
}

function sendMessage(content) {
    if (!stompClient || !selectedChatId || !content.trim()) {
        return;
    }

    const chatMessage = {
        chatId: selectedChatId,
        senderId: currentUser.id,
        content: content.trim()
    };

    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
    console.log('Message sent:', chatMessage);
}

// ==========================================
// UI Functions
// ==========================================
function displayMessage(message) {
    const isSent = message.senderId === currentUser.id;
    const senderName = message.senderUsername || (isSent ? currentUser.username : 'User');

    const messageEl = document.createElement('div');
    messageEl.className = `message ${isSent ? 'sent' : 'received'}`;

    messageEl.innerHTML = `
        <div class="message-avatar">${getInitials(senderName)}</div>
        <div class="message-content">
            <span class="message-sender">${senderName}</span>
            <div class="message-bubble">${escapeHtml(message.content)}</div>
            <span class="message-time">${formatTime(message.createdAt || new Date())}</span>
        </div>
    `;

    messagesContainer.appendChild(messageEl);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function scrollToBottom() {
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function clearMessages() {
    messagesContainer.innerHTML = '';
}

function selectChat(chatId) {
    // Deselect previous chat
    document.querySelectorAll('.chat-item').forEach(item => {
        item.classList.remove('active');
    });

    // Select new chat
    selectedChatId = chatId;
    const chatItem = document.querySelector(`.chat-item[data-chat-id="${chatId}"]`);
    if (chatItem) {
        chatItem.classList.add('active');
        chatItem.querySelector('.unread-badge')?.classList.add('hidden');
    }

    // Show chat area
    noChatSelected.classList.add('hidden');
    chatArea.classList.remove('hidden');

    // Update chat header
    const chat = chats.get(chatId);
    if (chat) {
        chatName.textContent = chat.name;
        chatAvatar.querySelector('span').textContent = getInitials(chat.name);
        chatStatus.textContent = `Chat ID: ${chatId.substring(0, 8)}...`;
    }

    // Clear and load messages
    clearMessages();
    if (chat && chat.messages) {
        chat.messages.forEach(msg => displayMessage(msg));
    }
    scrollToBottom();

    // Focus message input
    messageInput.focus();
}

function addChatToList(chatId, chatName, lastMessage = '', lastMessageTime = null) {
    // Check if chat already exists
    if (document.querySelector(`.chat-item[data-chat-id="${chatId}"]`)) {
        return;
    }

    const chatItem = document.createElement('li');
    chatItem.className = 'chat-item';
    chatItem.dataset.chatId = chatId;

    chatItem.innerHTML = `
        <div class="chat-item-avatar">${getInitials(chatName)}</div>
        <div class="chat-item-info">
            <div class="chat-item-name">${escapeHtml(chatName)}</div>
            <div class="chat-item-preview">${escapeHtml(lastMessage) || 'No messages yet'}</div>
        </div>
        <div class="chat-item-meta">
            <span class="chat-item-time">${lastMessageTime ? formatTime(lastMessageTime) : ''}</span>
            <span class="unread-badge hidden">0</span>
        </div>
    `;

    chatItem.addEventListener('click', () => selectChat(chatId));
    chatList.appendChild(chatItem);
}

function updateChatListItem(chatId) {
    const chatItem = document.querySelector(`.chat-item[data-chat-id="${chatId}"]`);
    const chat = chats.get(chatId);

    if (chatItem && chat) {
        const preview = chatItem.querySelector('.chat-item-preview');
        const time = chatItem.querySelector('.chat-item-time');

        if (preview) preview.textContent = chat.lastMessage || 'No messages yet';
        if (time) time.textContent = chat.lastMessageTime ? formatTime(chat.lastMessageTime) : '';
    }
}

function markChatAsUnread(chatId) {
    const chatItem = document.querySelector(`.chat-item[data-chat-id="${chatId}"]`);
    if (chatItem) {
        const badge = chatItem.querySelector('.unread-badge');
        if (badge) {
            badge.classList.remove('hidden');
            const count = parseInt(badge.textContent || '0') + 1;
            badge.textContent = count > 99 ? '99+' : count;
        }
    }
}

function addUserToList(user) {
    const userItem = document.createElement('li');
    userItem.className = 'user-item';
    userItem.dataset.userId = user.id;

    userItem.innerHTML = `
        <div class="user-item-avatar">${getInitials(user.username)}</div>
        <div class="user-item-info">
            <div class="user-item-name">${escapeHtml(user.username)}</div>
        </div>
        <div class="user-item-status"></div>
    `;

    userItem.addEventListener('click', () => startDirectChat(user));
    usersList.appendChild(userItem);
}

function startDirectChat(user) {
    // Create a deterministic chat ID for direct messages
    const sortedIds = [currentUser.id, user.id].sort();
    // const chatId = `dm-${sortedIds[0].substring(0, 8)}-${sortedIds[1].substring(0, 8)}`;
    const chatId = `5c55746e-8080-4a2f-abd6-f5d1761c29bd`;
    const chatNameStr = `Chat with ${user.username}`;

    createOrJoinChat(chatId, chatNameStr);
}

function createOrJoinChat(chatId, chatNameStr) {
    if (!chats.has(chatId)) {
        chats.set(chatId, {
            name: chatNameStr,
            messages: [],
            lastMessage: '',
            lastMessageTime: null,
            subscription: null
        });

        addChatToList(chatId, chatNameStr);
        subscribeToChat(chatId);
    }

    selectChat(chatId);
}

// ==========================================
// Modal Functions
// ==========================================
function openNewChatModal() {
    newChatModal.classList.remove('hidden');
    chatIdInput.value = generateUUID();
    chatNameInput.value = '';
    chatNameInput.focus();
}

function closeNewChatModal() {
    newChatModal.classList.add('hidden');
}

// ==========================================
// Event Listeners
// ==========================================
// Login form
loginForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const userId = userIdInput.value.trim();
    const username = usernameInput.value.trim();

    if (!userId || !username) {
        alert('Please fill in all fields');
        return;
    }

    // Validate UUID format (basic check)
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
    if (!uuidRegex.test(userId)) {
        alert('Please enter a valid UUID for User ID');
        return;
    }

    currentUser.id = userId;
    currentUser.username = username;

    // Update UI
    currentUserName.textContent = username;
    currentUserAvatar.querySelector('span').textContent = getInitials(username);

    // Show chat page
    loginPage.classList.add('hidden');
    chatPage.classList.remove('hidden');

    // Connect to WebSocket
    connect();

    // Load demo users (in real app, fetch from API)
    loadDemoUsers();
});

// Demo user buttons
demoBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        userIdInput.value = btn.dataset.userid;
        usernameInput.value = btn.dataset.username;
    });
});

// Message form
messageForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const content = messageInput.value.trim();
    if (content) {
        sendMessage(content);
        messageInput.value = '';
    }
});

// New chat button
newChatBtn.addEventListener('click', openNewChatModal);

// Close modal
closeModalBtn.addEventListener('click', closeNewChatModal);
newChatModal.querySelector('.modal-overlay').addEventListener('click', closeNewChatModal);

// Generate chat ID
generateChatIdBtn.addEventListener('click', () => {
    chatIdInput.value = generateUUID();
});

// New chat form
newChatForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const chatId = chatIdInput.value.trim();
    const chatNameStr = chatNameInput.value.trim();

    if (!chatId || !chatNameStr) {
        alert('Please fill in all fields');
        return;
    }

    createOrJoinChat(chatId, chatNameStr);
    closeNewChatModal();
});

// Logout
logoutBtn.addEventListener('click', () => {
    disconnect();

    // Clear state
    currentUser = {id: null, username: null};
    selectedChatId = null;
    chats.clear();
    subscribedChats.clear();

    // Clear UI
    chatList.innerHTML = '';
    usersList.innerHTML = '';
    clearMessages();

    // Show login page
    chatPage.classList.add('hidden');
    loginPage.classList.remove('hidden');

    // Clear form
    userIdInput.value = '';
    usernameInput.value = '';
});

// Handle page unload
window.addEventListener('beforeunload', () => {
    disconnect();
});

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
    // Escape to close modal
    if (e.key === 'Escape' && !newChatModal.classList.contains('hidden')) {
        closeNewChatModal();
    }
});

// ==========================================
// Demo Data
// ==========================================
function loadDemoUsers() {
    // Demo users (matching your seed data)
    const demoUsers = [
        {id: '550e8400-e29b-41d4-a716-446655440001', username: 'john_doe'},
        {id: '550e8400-e29b-41d4-a716-446655440002', username: 'jane_smith'},
        {id: '550e8400-e29b-41d4-a716-446655440003', username: 'mike_wilson'},
        {id: '550e8400-e29b-41d4-a716-446655440004', username: 'sarah_connor'},
        {id: '550e8400-e29b-41d4-a716-446655440005', username: 'alex_tech'}
    ];

    // Filter out current user and add to list
    demoUsers
        .filter(user => user.id !== currentUser.id)
        .forEach(user => addUserToList(user));
}

// ==========================================
// Initialize
// ==========================================
console.log('Chat application initialized');
