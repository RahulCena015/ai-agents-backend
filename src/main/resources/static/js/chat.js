/* ============================================================
   AI Agent — Chat UI Logic
   Handles sending messages, rendering responses, sidebar,
   auto-resize textarea, suggestion chips, and document upload.
   ============================================================ */

(function () {
    'use strict';

    // ---- DOM refs ----
    const chatForm      = document.getElementById('chatForm');
    const chatInput     = document.getElementById('chatInput');
    const sendBtn       = document.getElementById('sendBtn');
    const chatMessages  = document.getElementById('chatMessages');
    const emptyState    = document.getElementById('emptyState');
    const sidebar       = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarBackdrop = document.getElementById('sidebarBackdrop');
    const newChatBtn    = document.getElementById('newChatBtn');

    // Upload refs
    const uploadZone    = document.getElementById('uploadZone');
    const fileInput     = document.getElementById('fileInput');
    const uploadProgress = document.getElementById('uploadProgress');
    const uploadProgressText = document.getElementById('uploadProgressText');
    const docList       = document.getElementById('docList');

    const CHAT_API_URL   = '/api/gemini/chat';
    const UPLOAD_API_URL = '/api/documents/upload';
    const DOCS_API_URL   = '/api/documents';

    let isProcessing = false;

    // ---- Helpers ----

    function scrollToBottom() {
        requestAnimationFrame(() => {
            chatMessages.scrollTo({
                top: chatMessages.scrollHeight,
                behavior: 'smooth'
            });
        });
    }

    function autoResize() {
        chatInput.style.height = 'auto';
        chatInput.style.height = Math.min(chatInput.scrollHeight, 150) + 'px';
    }

    function setEmptyState(show) {
        emptyState.style.display = show ? 'flex' : 'none';
        if (show) {
            chatMessages.classList.remove('active');
        } else {
            chatMessages.classList.add('active');
        }
    }

    function escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    function formatResponse(text) {
        let formatted = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        formatted = formatted.replace(/\n/g, '<br>');
        return formatted;
    }

    // ---- Message Rendering ----

    function addUserMessage(text) {
        setEmptyState(false);
        const el = document.createElement('div');
        el.className = 'message user';
        el.innerHTML = `
            <div class="message-header">
                <div class="message-avatar user">You</div>
                <span class="message-sender">You</span>
            </div>
            <div class="message-body"><p>${escapeHtml(text)}</p></div>
        `;
        chatMessages.appendChild(el);
        scrollToBottom();
    }

    function addBotMessage(text) {
        const typing = chatMessages.querySelector('.typing-indicator');
        if (typing) typing.remove();

        const el = document.createElement('div');
        el.className = 'message bot';
        el.innerHTML = `
            <div class="message-header">
                <div class="message-avatar bot">AI</div>
                <span class="message-sender">AI Agent</span>
            </div>
            <div class="message-body"><p>${formatResponse(text)}</p></div>
        `;
        chatMessages.appendChild(el);
        scrollToBottom();
    }

    function showTyping() {
        const el = document.createElement('div');
        el.className = 'typing-indicator';
        el.innerHTML = `
            <div class="typing-dots">
                <div class="message-avatar bot">AI</div>
                <div class="dots"><span></span><span></span><span></span></div>
            </div>
        `;
        chatMessages.appendChild(el);
        scrollToBottom();
    }

    function showError(msg) {
        let toast = document.querySelector('.error-toast');
        if (!toast) {
            toast = document.createElement('div');
            toast.className = 'error-toast';
            document.body.appendChild(toast);
        }
        toast.textContent = msg;
        toast.classList.add('visible');
        setTimeout(() => toast.classList.remove('visible'), 4000);
    }

    // ---- Chat API ----

    async function sendMessage(prompt) {
        if (isProcessing || !prompt.trim()) return;
        isProcessing = true;
        sendBtn.disabled = true;

        addUserMessage(prompt);
        showTyping();

        try {
            const res = await fetch(CHAT_API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: prompt })
            });

            if (!res.ok) throw new Error(`Server responded with ${res.status}`);

            const data = await res.json();
            addBotMessage(data.response || 'No response received.');
        } catch (err) {
            const typing = chatMessages.querySelector('.typing-indicator');
            if (typing) typing.remove();
            showError('Failed to get a response. Please try again.');
            console.error('Chat error:', err);
        } finally {
            isProcessing = false;
            sendBtn.disabled = false;
            chatInput.focus();
        }
    }

    // ---- Document Upload ----

    function getFileExtension(name) {
        const ext = name.split('.').pop().toLowerCase();
        if (['pdf'].includes(ext)) return 'pdf';
        if (['txt', 'text', 'csv'].includes(ext)) return 'txt';
        if (['doc', 'docx'].includes(ext)) return 'docx';
        return 'txt';
    }

    async function uploadFile(file) {
        uploadProgress.classList.add('active');
        uploadProgressText.textContent = `Ingesting "${file.name}"...`;

        const formData = new FormData();
        formData.append('file', file);

        try {
            const res = await fetch(UPLOAD_API_URL, {
                method: 'POST',
                body: formData
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                throw new Error(errData.error || `Upload failed (${res.status})`);
            }

            const data = await res.json();
            uploadProgressText.textContent = data.message || 'Upload complete!';
            setTimeout(() => uploadProgress.classList.remove('active'), 2000);

            // Refresh document list
            loadDocuments();
        } catch (err) {
            uploadProgressText.textContent = 'Upload failed: ' + err.message;
            setTimeout(() => uploadProgress.classList.remove('active'), 3000);
            showError('Failed to upload document: ' + err.message);
            console.error('Upload error:', err);
        }
    }

    function renderDocList(docs) {
        docList.innerHTML = '';
        docs.forEach(name => {
            const ext = getFileExtension(name);
            const item = document.createElement('div');
            item.className = 'doc-item';
            item.innerHTML = `
                <div class="doc-icon ${ext}">${ext}</div>
                <span class="doc-name" title="${escapeHtml(name)}">${escapeHtml(name)}</span>
                <button class="doc-delete" data-name="${escapeHtml(name)}" title="Remove document" aria-label="Remove ${escapeHtml(name)}">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                        <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                </button>
            `;
            docList.appendChild(item);
        });

        // Attach delete handlers
        docList.querySelectorAll('.doc-delete').forEach(btn => {
            btn.addEventListener('click', async function () {
                const name = this.dataset.name;
                try {
                    await fetch(DOCS_API_URL + '/' + encodeURIComponent(name), { method: 'DELETE' });
                    loadDocuments();
                } catch (err) {
                    showError('Failed to remove document');
                }
            });
        });
    }

    async function loadDocuments() {
        try {
            const res = await fetch(DOCS_API_URL);
            if (res.ok) {
                const docs = await res.json();
                renderDocList(docs);
            }
        } catch (err) {
            console.error('Failed to load documents:', err);
        }
    }

    // ---- Upload Zone Events ----

    uploadZone.addEventListener('click', () => fileInput.click());

    fileInput.addEventListener('change', function () {
        if (this.files.length > 0) {
            uploadFile(this.files[0]);
            this.value = '';
        }
    });

    // Drag and drop
    uploadZone.addEventListener('dragover', function (e) {
        e.preventDefault();
        this.classList.add('drag-over');
    });

    uploadZone.addEventListener('dragleave', function () {
        this.classList.remove('drag-over');
    });

    uploadZone.addEventListener('drop', function (e) {
        e.preventDefault();
        this.classList.remove('drag-over');
        if (e.dataTransfer.files.length > 0) {
            uploadFile(e.dataTransfer.files[0]);
        }
    });

    // ---- Chat Form Events ----

    chatForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const text = chatInput.value.trim();
        if (!text) return;
        chatInput.value = '';
        chatInput.style.height = 'auto';
        sendMessage(text);
    });

    chatInput.addEventListener('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            chatForm.dispatchEvent(new Event('submit'));
        }
    });

    chatInput.addEventListener('input', autoResize);

    // Suggestion chips
    document.querySelectorAll('[data-prompt]').forEach(btn => {
        btn.addEventListener('click', function () {
            const prompt = this.dataset.prompt;
            chatInput.value = '';
            sidebar.classList.remove('open');
            sidebarBackdrop.classList.remove('open');
            sendMessage(prompt);
        });
    });

    // New chat
    newChatBtn.addEventListener('click', function () {
        chatMessages.innerHTML = '';
        setEmptyState(true);
        chatInput.value = '';
        chatInput.style.height = 'auto';
        sidebar.classList.remove('open');
        sidebarBackdrop.classList.remove('open');
    });

    // Sidebar toggle
    sidebarToggle.addEventListener('click', function () {
        sidebar.classList.toggle('open');
        sidebarBackdrop.classList.toggle('open');
    });

    sidebarBackdrop.addEventListener('click', function () {
        sidebar.classList.remove('open');
        sidebarBackdrop.classList.remove('open');
    });

    // ---- Init ----
    chatInput.focus();
    loadDocuments();
})();
