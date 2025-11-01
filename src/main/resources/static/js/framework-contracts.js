/**
 * eContract KI - Framework Contracts JavaScript
 */

const API_BASE = '/econtract/api/v1';
let currentFrameworkId = null;

// Beim Laden der Seite
document.addEventListener('DOMContentLoaded', () => {
    loadFrameworkStats();
    loadFrameworkContracts();
});

/**
 * Lädt Rahmenvertrags-Statistiken
 */
async function loadFrameworkStats() {
    try {
        const response = await fetch(`${API_BASE}/framework-contracts/stats`);
        if (!response.ok) throw new Error('Failed to load stats');
        
        const stats = await response.json();
        
        document.getElementById('totalFrameworks').textContent = stats.total || 0;
        document.getElementById('activeFrameworks').textContent = stats.active || 0;
        document.getElementById('totalVolume').textContent = '0 €'; // TODO: Calculate
        document.getElementById('totalChildContracts').textContent = '0';
        
    } catch (error) {
        console.error('Error loading framework stats:', error);
    }
}

/**
 * Lädt alle Rahmenverträge
 */
async function loadFrameworkContracts() {
    try {
        const response = await fetch(`${API_BASE}/framework-contracts?size=100`);
        if (!response.ok) throw new Error('Failed to load frameworks');
        
        const data = await response.json();
        const frameworks = data.content || [];
        
        renderFrameworkContracts(frameworks);
        
    } catch (error) {
        console.error('Error loading frameworks:', error);
        document.getElementById('frameworkTableBody').innerHTML = 
            '<tr><td colspan="9" class="no-data">Fehler beim Laden</td></tr>';
    }
}

/**
 * Rendert Rahmenverträge
 */
function renderFrameworkContracts(frameworks) {
    const tbody = document.getElementById('frameworkTableBody');
    
    if (frameworks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="no-data">Keine Rahmenverträge vorhanden</td></tr>';
        return;
    }
    
    tbody.innerHTML = frameworks.map(fw => {
        const statusBadge = getStatusBadge(fw.status);
        const runtime = fw.startDate && fw.endDate 
            ? `${formatDate(fw.startDate)} - ${formatDate(fw.endDate)}`
            : '-';
        
        return `
            <tr>
                <td><strong>${fw.frameworkNumber}</strong></td>
                <td>${fw.title}</td>
                <td>${fw.partnerName}</td>
                <td>${getTypeLabel(fw.contractType)}</td>
                <td>${formatCurrency(fw.totalVolume)} ${fw.currency}</td>
                <td>${runtime}</td>
                <td>
                    <button class="btn-link" onclick="viewChildContracts(${fw.id})">
                        <span id="childCount${fw.id}">0</span> Verträge
                    </button>
                </td>
                <td>${statusBadge}</td>
                <td>
                    <button class="btn-icon" onclick="viewChildContracts(${fw.id})" title="Einzelverträge">📋</button>
                    <button class="btn-icon" onclick="editFramework(${fw.id})" title="Bearbeiten">✏️</button>
                    <button class="btn-icon" onclick="deleteFramework(${fw.id})" title="Löschen">🗑️</button>
                </td>
            </tr>
        `;
    }).join('');
    
    // Lade Anzahl der Einzelverträge für jeden Rahmenvertrag
    frameworks.forEach(fw => loadChildContractCount(fw.id));
}

/**
 * Lädt Anzahl der Einzelverträge
 */
async function loadChildContractCount(frameworkId) {
    try {
        const response = await fetch(`${API_BASE}/framework-contracts/${frameworkId}/child-contracts`);
        if (!response.ok) return;
        
        const contracts = await response.json();
        const countElement = document.getElementById(`childCount${frameworkId}`);
        if (countElement) {
            countElement.textContent = contracts.length;
        }
    } catch (error) {
        console.error('Error loading child contract count:', error);
    }
}

/**
 * Öffnet Rahmenvertrag-Modal
 */
function openFrameworkModal(id = null) {
    document.getElementById('modalTitle').textContent = id ? 'Rahmenvertrag bearbeiten' : 'Neuer Rahmenvertrag';
    document.getElementById('frameworkForm').reset();
    document.getElementById('frameworkId').value = id || '';
    
    if (id) {
        loadFrameworkData(id);
    }
    
    document.getElementById('frameworkModal').style.display = 'block';
}

/**
 * Lädt Rahmenvertragsdaten
 */
async function loadFrameworkData(id) {
    try {
        const response = await fetch(`${API_BASE}/framework-contracts/${id}`);
        if (!response.ok) throw new Error('Failed to load framework');
        
        const framework = await response.json();
        const form = document.getElementById('frameworkForm');
        
        form.title.value = framework.title || '';
        form.partnerName.value = framework.partnerName || '';
        form.contractType.value = framework.contractType || '';
        form.department.value = framework.department || '';
        form.startDate.value = framework.startDate || '';
        form.endDate.value = framework.endDate || '';
        form.totalVolume.value = framework.totalVolume || '';
        form.currency.value = framework.currency || 'EUR';
        form.description.value = framework.description || '';
        
    } catch (error) {
        console.error('Error loading framework data:', error);
        alert('❌ Fehler beim Laden des Rahmenvertrags');
    }
}

/**
 * Schließt Rahmenvertrag-Modal
 */
function closeFrameworkModal() {
    document.getElementById('frameworkModal').style.display = 'none';
    document.getElementById('frameworkForm').reset();
}

/**
 * Speichert Rahmenvertrag
 */
async function saveFramework(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const frameworkId = document.getElementById('frameworkId').value;
    
    const framework = {
        title: formData.get('title'),
        partnerName: formData.get('partnerName'),
        contractType: formData.get('contractType'),
        department: formData.get('department'),
        startDate: formData.get('startDate') || null,
        endDate: formData.get('endDate') || null,
        totalVolume: formData.get('totalVolume') ? parseFloat(formData.get('totalVolume')) : null,
        currency: formData.get('currency'),
        description: formData.get('description'),
        status: 'DRAFT'
    };
    
    try {
        const url = frameworkId 
            ? `${API_BASE}/framework-contracts/${frameworkId}`
            : `${API_BASE}/framework-contracts`;
        
        const method = frameworkId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(framework)
        });
        
        if (!response.ok) throw new Error('Failed to save framework');
        
        alert('✅ Rahmenvertrag erfolgreich gespeichert!');
        closeFrameworkModal();
        loadFrameworkContracts();
        loadFrameworkStats();
        
    } catch (error) {
        console.error('Error saving framework:', error);
        alert('❌ Fehler beim Speichern: ' + error.message);
    }
}

/**
 * Bearbeitet Rahmenvertrag
 */
function editFramework(id) {
    openFrameworkModal(id);
}

/**
 * Löscht Rahmenvertrag
 */
async function deleteFramework(id) {
    if (!confirm('Möchten Sie diesen Rahmenvertrag wirklich löschen?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/framework-contracts/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to delete framework');
        
        alert('✅ Rahmenvertrag gelöscht!');
        loadFrameworkContracts();
        loadFrameworkStats();
        
    } catch (error) {
        console.error('Error deleting framework:', error);
        alert('❌ Fehler beim Löschen: ' + error.message);
    }
}

/**
 * Zeigt Einzelverträge an
 */
async function viewChildContracts(frameworkId) {
    currentFrameworkId = frameworkId;
    
    try {
        // Lade Rahmenvertrag
        const fwResponse = await fetch(`${API_BASE}/framework-contracts/${frameworkId}`);
        if (!fwResponse.ok) throw new Error('Failed to load framework');
        const framework = await fwResponse.json();
        
        // Lade Einzelverträge
        const childResponse = await fetch(`${API_BASE}/framework-contracts/${frameworkId}/child-contracts`);
        if (!childResponse.ok) throw new Error('Failed to load child contracts');
        const childContracts = await childResponse.json();
        
        // Lade Volumen-Nutzung
        const volumeResponse = await fetch(`${API_BASE}/framework-contracts/${frameworkId}/volume-usage`);
        const volumeData = volumeResponse.ok ? await volumeResponse.json() : null;
        
        // Zeige Rahmenvertrag-Info
        document.getElementById('frameworkInfo').innerHTML = `
            <p><strong>Rahmenvertragsnummer:</strong> ${framework.frameworkNumber}</p>
            <p><strong>Titel:</strong> ${framework.title}</p>
            <p><strong>Partner:</strong> ${framework.partnerName}</p>
            <p><strong>Gesamtvolumen:</strong> ${formatCurrency(framework.totalVolume)} ${framework.currency}</p>
        `;
        
        // Zeige Volumen-Nutzung
        if (volumeData) {
            const percentage = volumeData.usagePercentage || 0;
            const progressClass = percentage > 90 ? 'danger' : percentage > 75 ? 'warning' : 'success';
            
            document.getElementById('volumeUsage').innerHTML = `
                <h4>Volumen-Nutzung</h4>
                <div class="progress-bar-large">
                    <div class="progress-fill ${progressClass}" style="width: ${percentage}%"></div>
                </div>
                <div class="volume-details">
                    <p><strong>Genutzt:</strong> ${formatCurrency(volumeData.usedVolume)} ${volumeData.currency}</p>
                    <p><strong>Verfügbar:</strong> ${formatCurrency(volumeData.remainingVolume)} ${volumeData.currency}</p>
                    <p><strong>Auslastung:</strong> ${percentage.toFixed(1)}%</p>
                </div>
            `;
        }
        
        // Rendere Einzelverträge
        renderChildContracts(childContracts);
        
        document.getElementById('childContractsModal').style.display = 'block';
        
    } catch (error) {
        console.error('Error viewing child contracts:', error);
        alert('❌ Fehler beim Laden der Einzelverträge');
    }
}

/**
 * Rendert Einzelverträge
 */
function renderChildContracts(contracts) {
    const tbody = document.getElementById('childContractsTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="no-data">Keine Einzelverträge zugeordnet</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => {
        const runtime = contract.startDate && contract.endDate 
            ? `${formatDate(contract.startDate)} - ${formatDate(contract.endDate)}`
            : '-';
        
        return `
            <tr>
                <td><strong>${contract.contractNumber}</strong></td>
                <td>${contract.title}</td>
                <td>${formatCurrency(contract.contractValue)} ${contract.currency}</td>
                <td>${runtime}</td>
                <td>${getStatusBadge(contract.status)}</td>
                <td>
                    <button class="btn-icon" onclick="removeChildContract(${contract.id})" title="Entfernen">❌</button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Schließt Einzelverträge-Modal
 */
function closeChildContractsModal() {
    document.getElementById('childContractsModal').style.display = 'none';
    currentFrameworkId = null;
}

/**
 * Fügt Einzelvertrag hinzu
 */
function addChildContract() {
    alert('Diese Funktion wird in Kürze verfügbar sein.\nSie können Verträge über die Vertragsliste einem Rahmenvertrag zuordnen.');
}

/**
 * Entfernt Einzelvertrag
 */
async function removeChildContract(contractId) {
    if (!confirm('Möchten Sie diesen Einzelvertrag vom Rahmenvertrag entfernen?')) {
        return;
    }
    
    try {
        const response = await fetch(
            `${API_BASE}/framework-contracts/${currentFrameworkId}/remove-child/${contractId}`,
            { method: 'DELETE' }
        );
        
        if (!response.ok) throw new Error('Failed to remove child contract');
        
        alert('✅ Einzelvertrag entfernt!');
        viewChildContracts(currentFrameworkId);
        
    } catch (error) {
        console.error('Error removing child contract:', error);
        alert('❌ Fehler beim Entfernen: ' + error.message);
    }
}

/**
 * Sucht Rahmenverträge
 */
async function searchFrameworks() {
    const query = document.getElementById('searchInput').value;
    if (!query) {
        loadFrameworkContracts();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/framework-contracts/search?q=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error('Search failed');
        
        const frameworks = await response.json();
        renderFrameworkContracts(frameworks);
        
    } catch (error) {
        console.error('Error searching frameworks:', error);
    }
}

/**
 * Behandelt Suche bei Enter
 */
function handleSearch(event) {
    if (event.key === 'Enter') {
        searchFrameworks();
    }
}

// Helper Functions

function getStatusBadge(status) {
    const badges = {
        'DRAFT': '<span class="badge badge-secondary">Entwurf</span>',
        'IN_APPROVAL': '<span class="badge badge-warning">In Genehmigung</span>',
        'ACTIVE': '<span class="badge badge-success">Aktiv</span>',
        'EXPIRED': '<span class="badge badge-danger">Abgelaufen</span>',
        'TERMINATED': '<span class="badge badge-danger">Gekündigt</span>'
    };
    return badges[status] || '<span class="badge">Unbekannt</span>';
}

function getTypeLabel(type) {
    const labels = {
        'SUPPLIER': 'Lieferant',
        'CUSTOMER': 'Kunde',
        'SERVICE': 'Dienstleistung',
        'NDA': 'NDA'
    };
    return labels[type] || type;
}

function formatCurrency(value) {
    if (!value) return '0,00';
    return new Intl.NumberFormat('de-DE', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value);
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE');
}

