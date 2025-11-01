// Contract Import JavaScript

let selectedFiles = [];

// Initialisierung
document.addEventListener('DOMContentLoaded', function() {
    setupUploadZone();
    loadStatistics();
    loadWorkQueue();
    
    // Auto-Refresh alle 10 Sekunden
    setInterval(() => {
        loadStatistics();
        if (document.getElementById('tab-queue').classList.contains('active')) {
            loadWorkQueue();
        }
    }, 10000);
});

// Upload-Zone einrichten
function setupUploadZone() {
    const uploadZone = document.getElementById('uploadZone');
    const fileInput = document.getElementById('fileInput');
    
    // Click-Handler
    uploadZone.addEventListener('click', () => {
        fileInput.click();
    });
    
    // File-Input-Handler
    fileInput.addEventListener('change', (e) => {
        handleFiles(e.target.files);
    });
    
    // Drag & Drop
    uploadZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadZone.classList.add('dragover');
    });
    
    uploadZone.addEventListener('dragleave', () => {
        uploadZone.classList.remove('dragover');
    });
    
    uploadZone.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
        handleFiles(e.dataTransfer.files);
    });
}

// Dateien verarbeiten
function handleFiles(files) {
    selectedFiles = Array.from(files);
    displaySelectedFiles();
}

// Ausgewählte Dateien anzeigen
function displaySelectedFiles() {
    const container = document.getElementById('selectedFiles');
    const list = document.getElementById('filesList');
    
    if (selectedFiles.length === 0) {
        container.style.display = 'none';
        return;
    }
    
    container.style.display = 'block';
    list.innerHTML = '';
    
    selectedFiles.forEach((file, index) => {
        const item = document.createElement('div');
        item.className = 'file-item';
        item.innerHTML = `
            <div class="file-info">
                <div class="file-icon">📄</div>
                <div class="file-details">
                    <h4>${file.name}</h4>
                    <p>${formatFileSize(file.size)} • ${file.type || 'Unbekannt'}</p>
                </div>
            </div>
            <button class="btn btn-sm btn-danger" onclick="removeFile(${index})">🗑️</button>
        `;
        list.appendChild(item);
    });
}

// Datei entfernen
function removeFile(index) {
    selectedFiles.splice(index, 1);
    displaySelectedFiles();
}

// Alle Dateien löschen
function clearFiles() {
    selectedFiles = [];
    document.getElementById('fileInput').value = '';
    displaySelectedFiles();
}

// Dateien hochladen
async function uploadFiles() {
    if (selectedFiles.length === 0) {
        alert('Bitte wählen Sie mindestens eine Datei aus');
        return;
    }
    
    const formData = new FormData();
    
    if (selectedFiles.length === 1) {
        // Einzelupload
        formData.append('file', selectedFiles[0]);
        formData.append('uploadedBy', 'admin');
        
        try {
            const response = await fetch('/econtract/api/v1/import/single', {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) throw new Error('Upload fehlgeschlagen');
            
            const result = await response.json();
            alert('Datei erfolgreich hochgeladen und wird verarbeitet!');
            clearFiles();
            loadStatistics();
            
            // Zum Arbeitsvorrat wechseln
            setTimeout(() => {
                switchTab('queue');
                loadWorkQueue();
            }, 2000);
            
        } catch (error) {
            console.error('Fehler beim Upload:', error);
            alert('Fehler beim Upload: ' + error.message);
        }
        
    } else {
        // Batch-Upload
        selectedFiles.forEach(file => {
            formData.append('files', file);
        });
        formData.append('batchName', `Batch ${new Date().toLocaleString()}`);
        formData.append('uploadedBy', 'admin');
        
        try {
            const response = await fetch('/econtract/api/v1/import/batch', {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) throw new Error('Batch-Upload fehlgeschlagen');
            
            const result = await response.json();
            alert(`${selectedFiles.length} Dateien erfolgreich hochgeladen und werden verarbeitet!`);
            clearFiles();
            loadStatistics();
            
            // Zum Arbeitsvorrat wechseln
            setTimeout(() => {
                switchTab('queue');
                loadWorkQueue();
            }, 2000);
            
        } catch (error) {
            console.error('Fehler beim Batch-Upload:', error);
            alert('Fehler beim Batch-Upload: ' + error.message);
        }
    }
}

// Statistiken laden
async function loadStatistics() {
    try {
        const response = await fetch('/econtract/api/v1/import/stats');
        if (!response.ok) throw new Error('Fehler beim Laden der Statistiken');
        
        const stats = await response.json();
        
        document.getElementById('stat-pending').textContent = stats.pending || 0;
        document.getElementById('stat-processing').textContent = stats.processing || 0;
        document.getElementById('stat-extracted').textContent = stats.extracted || 0;
        document.getElementById('stat-completed').textContent = stats.completed || 0;
        
        // Queue-Count aktualisieren
        document.getElementById('queueCount').textContent = stats.extracted || 0;
        
    } catch (error) {
        console.error('Fehler beim Laden der Statistiken:', error);
    }
}

// Arbeitsvorrat laden
async function loadWorkQueue() {
    try {
        const response = await fetch('/econtract/api/v1/import/queue');
        if (!response.ok) throw new Error('Fehler beim Laden des Arbeitsvorrats');
        
        const queue = await response.json();
        displayWorkQueue(queue);
        
    } catch (error) {
        console.error('Fehler beim Laden des Arbeitsvorrats:', error);
        document.getElementById('queueList').innerHTML = 
            '<p class="text-danger">Fehler beim Laden des Arbeitsvorrats</p>';
    }
}

// Arbeitsvorrat anzeigen
function displayWorkQueue(queue) {
    const container = document.getElementById('queueList');
    
    if (queue.length === 0) {
        container.innerHTML = '<p class="text-muted">Keine Einträge im Arbeitsvorrat</p>';
        return;
    }
    
    container.innerHTML = '';
    
    queue.forEach(item => {
        const queueItem = document.createElement('div');
        queueItem.className = 'queue-item';
        
        const extractedData = item.extractedData ? JSON.parse(item.extractedData) : {};
        
        queueItem.innerHTML = `
            <div class="queue-header">
                <div>
                    <h4>📄 ${item.originalFilename}</h4>
                    <p class="text-muted">
                        Hochgeladen: ${formatDate(item.createdAt)} • 
                        ${formatFileSize(item.fileSize)}
                    </p>
                </div>
                <span class="status-badge status-${item.status.toLowerCase()}">
                    ${getStatusText(item.status)}
                </span>
            </div>
            
            <div class="extracted-data">
                <strong>📋 Extrahierte Daten:</strong>
                <div style="margin-top: 10px; display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
                    ${extractedData.title ? `<div><strong>Titel:</strong> ${extractedData.title}</div>` : ''}
                    ${extractedData.partner ? `<div><strong>Partner:</strong> ${extractedData.partner}</div>` : ''}
                    ${extractedData.type ? `<div><strong>Typ:</strong> ${getContractTypeText(extractedData.type)}</div>` : ''}
                    ${extractedData.department ? `<div><strong>Abteilung:</strong> ${extractedData.department}</div>` : ''}
                    ${extractedData.startDate ? `<div><strong>Start:</strong> ${formatDate(extractedData.startDate)}</div>` : ''}
                    ${extractedData.endDate ? `<div><strong>Ende:</strong> ${formatDate(extractedData.endDate)}</div>` : ''}
                    ${extractedData.value ? `<div><strong>Wert:</strong> ${formatCurrency(extractedData.value)}</div>` : ''}
                    ${extractedData.description ? `<div style="grid-column: 1 / -1;"><strong>Beschreibung:</strong> ${extractedData.description}</div>` : ''}
                </div>
            </div>
            
            <div class="queue-actions">
                <button class="btn btn-success" onclick="approveQueueItem(${item.id})">
                    ✅ Genehmigen & Vertrag erstellen
                </button>
                <button class="btn btn-warning" onclick="editQueueItem(${item.id})">
                    ✏️ Bearbeiten
                </button>
                <button class="btn btn-danger" onclick="rejectQueueItem(${item.id})">
                    ❌ Ablehnen
                </button>
            </div>
        `;
        
        container.appendChild(queueItem);
    });
}

// Queue-Item genehmigen
async function approveQueueItem(id) {
    if (!confirm('Möchten Sie diesen Vertrag genehmigen und erstellen?')) return;
    
    try {
        const response = await fetch(`/econtract/api/v1/import/queue/${id}/approve?reviewedBy=admin`, {
            method: 'POST'
        });
        
        if (!response.ok) throw new Error('Genehmigung fehlgeschlagen');
        
        alert('Vertrag wurde genehmigt!');
        loadWorkQueue();
        loadStatistics();
        
    } catch (error) {
        console.error('Fehler bei Genehmigung:', error);
        alert('Fehler bei Genehmigung: ' + error.message);
    }
}

// Queue-Item bearbeiten
async function editQueueItem(id) {
    try {
        // Queue-Item Daten laden
        const response = await fetch('/econtract/api/v1/import/queue');
        const queue = await response.json();
        const item = queue.find(q => q.id === id);
        
        if (!item) {
            alert('Item nicht gefunden');
            return;
        }
        
        const extractedData = item.extractedData ? JSON.parse(item.extractedData) : {};
        openEditModal(id, extractedData);
        
    } catch (error) {
        console.error('Fehler beim Laden der Daten:', error);
        alert('Fehler beim Laden der Daten');
    }
}

// Queue-Item ablehnen
async function rejectQueueItem(id) {
    const reason = prompt('Grund für Ablehnung:');
    if (!reason) return;
    
    try {
        const response = await fetch(
            `/econtract/api/v1/import/queue/${id}/reject?reviewedBy=admin&reason=${encodeURIComponent(reason)}`,
            { method: 'POST' }
        );
        
        if (!response.ok) throw new Error('Ablehnung fehlgeschlagen');
        
        alert('Vertrag wurde abgelehnt');
        loadWorkQueue();
        loadStatistics();
        
    } catch (error) {
        console.error('Fehler bei Ablehnung:', error);
        alert('Fehler bei Ablehnung: ' + error.message);
    }
}

// Tab-Wechsel
function switchTab(tabName) {
    // Alle Tabs deaktivieren
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // Aktiven Tab aktivieren
    event.target.classList.add('active');
    document.getElementById('tab-' + tabName).classList.add('active');
    
    // Daten laden
    if (tabName === 'queue') {
        loadWorkQueue();
    } else if (tabName === 'history') {
        loadHistory();
    }
}

// Hilfsfunktionen
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('de-DE');
}

function getStatusText(status) {
    const statusTexts = {
        'PENDING': 'Wartend',
        'PROCESSING': 'Wird verarbeitet',
        'EXTRACTED': 'Extrahiert',
        'APPROVED': 'Genehmigt',
        'REJECTED': 'Abgelehnt',
        'COMPLETED': 'Abgeschlossen',
        'ERROR': 'Fehler'
    };
    return statusTexts[status] || status;
}



function getContractTypeText(type) {
    const types = {
        'SUPPLIER': 'Lieferantenvertrag',
        'CUSTOMER': 'Kundenvertrag',
        'SERVICE': 'Dienstleistungsvertrag',
        'NDA': 'Geheimhaltungsvereinbarung',
        'EMPLOYMENT': 'Arbeitsvertrag'
    };
    return types[type] || type;
}

function formatCurrency(value) {
    if (!value) return '-';
    return new Intl.NumberFormat('de-DE', { 
        style: 'currency', 
        currency: 'EUR' 
    }).format(value);
}

