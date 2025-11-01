/**
 * eContract KI - Frontend JavaScript
 * Vertragsverwaltung
 */

const API_BASE = '/econtract/api/v1';
let currentPage = 0;
const pageSize = 20;

// Beim Laden der Seite
document.addEventListener('DOMContentLoaded', () => {
    loadStatistics();
    loadContracts();
});

/**
 * Lädt Dashboard-Statistiken
 */
async function loadStatistics() {
    try {
        const response = await fetch(`${API_BASE}/contracts/stats`);
        if (!response.ok) throw new Error('Failed to load statistics');
        
        const stats = await response.json();
        document.getElementById('totalContracts').textContent = stats.total || 0;
        document.getElementById('draftContracts').textContent = stats.draft || 0;
        document.getElementById('approvalContracts').textContent = stats.in_approval || 0;
        document.getElementById('activeContracts').textContent = stats.active || 0;
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

/**
 * Lädt Verträge
 */
async function loadContracts(page = 0) {
    currentPage = page;
    const statusFilter = document.getElementById('statusFilter').value;
    const typeFilter = document.getElementById('typeFilter').value;
    
    let url = `${API_BASE}/contracts?page=${page}&size=${pageSize}&sortBy=createdAt&sortDir=DESC`;
    if (statusFilter) url += `&status=${statusFilter}`;
    if (typeFilter) url += `&contractType=${typeFilter}`;
    
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('Failed to load contracts');
        
        const data = await response.json();
        renderContracts(data.content);
        renderPagination(data);
    } catch (error) {
        console.error('Error loading contracts:', error);
        showError('Verträge konnten nicht geladen werden');
    }
}

/**
 * Rendert Verträge in Tabelle
 */
function renderContracts(contracts) {
    const tbody = document.getElementById('contractsTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="no-data">Keine Verträge gefunden</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => `
        <tr>
            <td><strong>${contract.contractNumber}</strong></td>
            <td>${contract.title}</td>
            <td>${formatContractType(contract.contractType)}</td>
            <td><span class="status-badge status-${contract.status.toLowerCase()}">${formatStatus(contract.status)}</span></td>
            <td>${contract.partnerName}</td>
            <td>${formatDate(contract.endDate)}</td>
            <td>${formatCurrency(contract.contractValue, contract.currency)}</td>
            <td class="actions">
                <button class="btn-icon" onclick="viewContract(${contract.id})" title="Ansehen">👁️</button>
                <button class="btn-icon" onclick="editContract(${contract.id})" title="Bearbeiten">✏️</button>
                <button class="btn-icon" onclick="deleteContract(${contract.id})" title="Löschen">🗑️</button>
            </td>
        </tr>
    `).join('');
}

/**
 * Rendert Pagination
 */
function renderPagination(data) {
    const pagination = document.getElementById('pagination');
    const totalPages = data.totalPages;
    
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let html = '<div class="pagination-controls">';
    
    // Previous button
    if (currentPage > 0) {
        html += `<button onclick="loadContracts(${currentPage - 1})">← Zurück</button>`;
    }
    
    // Page numbers
    html += `<span>Seite ${currentPage + 1} von ${totalPages}</span>`;
    
    // Next button
    if (currentPage < totalPages - 1) {
        html += `<button onclick="loadContracts(${currentPage + 1})">Weiter →</button>`;
    }
    
    html += '</div>';
    pagination.innerHTML = html;
}

/**
 * Sucht Verträge
 */
async function searchContracts() {
    const query = document.getElementById('searchInput').value.trim();
    if (!query) {
        loadContracts();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/contracts/search?q=${encodeURIComponent(query)}&page=0&size=${pageSize}`);
        if (!response.ok) throw new Error('Search failed');
        
        const data = await response.json();
        renderContracts(data.content);
        renderPagination(data);
    } catch (error) {
        console.error('Error searching contracts:', error);
        showError('Suche fehlgeschlagen');
    }
}

/**
 * Handle Enter-Taste in Suchfeld
 */
function handleSearch(event) {
    if (event.key === 'Enter') {
        searchContracts();
    }
}

/**
 * Wendet Filter an
 */
function applyFilters() {
    loadContracts(0);
}

/**
 * Zeigt Create Modal
 */
function showCreateModal() {
    document.getElementById('createModal').style.display = 'block';
}

/**
 * Schließt Create Modal
 */
function closeCreateModal() {
    document.getElementById('createModal').style.display = 'none';
    document.getElementById('createContractForm').reset();
}

/**
 * Erstellt neuen Vertrag
 */
async function createContract(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    
    const contractData = {
        title: formData.get('title'),
        contractType: formData.get('contractType'),
        partnerName: formData.get('partnerName'),
        startDate: formData.get('startDate') || null,
        endDate: formData.get('endDate') || null,
        contractValue: formData.get('contractValue') ? parseFloat(formData.get('contractValue')) : null,
        currency: formData.get('currency') || 'EUR',
        department: formData.get('department') || null,
        ownerUserId: 1, // TODO: Aktuellen User verwenden
        createdBy: 1
    };
    
    try {
        const response = await fetch(`${API_BASE}/contracts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(contractData)
        });
        
        if (!response.ok) throw new Error('Failed to create contract');
        
        const created = await response.json();
        showSuccess('Vertrag erfolgreich erstellt!');
        closeCreateModal();
        loadContracts();
        loadStatistics();
    } catch (error) {
        console.error('Error creating contract:', error);
        showError('Fehler beim Erstellen des Vertrags');
    }
}

/**
 * Zeigt Vertrag an
 */
function viewContract(id) {
    window.location.href = `/econtract/contract-detail.html?id=${id}`;
}

/**
 * Bearbeitet Vertrag
 */
function editContract(id) {
    window.location.href = `/econtract/contract-edit.html?id=${id}`;
}

/**
 * Löscht Vertrag
 */
async function deleteContract(id) {
    if (!confirm('Möchten Sie diesen Vertrag wirklich löschen?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/contracts/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to delete contract');
        
        showSuccess('Vertrag erfolgreich gelöscht');
        loadContracts();
        loadStatistics();
    } catch (error) {
        console.error('Error deleting contract:', error);
        showError('Fehler beim Löschen des Vertrags');
    }
}

/**
 * Formatiert Datum
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE');
}

/**
 * Formatiert Währung
 */
function formatCurrency(value, currency) {
    if (!value) return '-';
    return new Intl.NumberFormat('de-DE', {
        style: 'currency',
        currency: currency || 'EUR'
    }).format(value);
}

/**
 * Formatiert Vertragstyp
 */
function formatContractType(type) {
    const types = {
        'SUPPLIER': 'Lieferant',
        'CUSTOMER': 'Kunde',
        'SERVICE': 'Dienstleistung',
        'NDA': 'NDA',
        'EMPLOYMENT': 'Arbeitsvertrag'
    };
    return types[type] || type;
}

/**
 * Formatiert Status
 */
function formatStatus(status) {
    const statuses = {
        'DRAFT': 'Entwurf',
        'IN_NEGOTIATION': 'In Verhandlung',
        'IN_APPROVAL': 'In Genehmigung',
        'APPROVED': 'Genehmigt',
        'ACTIVE': 'Aktiv',
        'EXPIRED': 'Abgelaufen',
        'TERMINATED': 'Beendet'
    };
    return statuses[status] || status;
}

/**
 * Zeigt Erfolgsmeldung
 */
function showSuccess(message) {
    alert('✅ ' + message);
}

/**
 * Zeigt Fehlermeldung
 */
function showError(message) {
    alert('❌ ' + message);
}

