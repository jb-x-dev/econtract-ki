/**
 * Service Records Management
 * eContract KI - Billing Module
 */

let serviceRecords = [];
let contracts = [];
let serviceCategories = [];
let currentUserId = 1; // TODO: Get from session

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadContracts();
    loadServiceCategories();
    loadServiceRecords();
    
    // Set default date to today
    document.getElementById('serviceDate').valueAsDate = new Date();
});

/**
 * Load all contracts
 */
async function loadContracts() {
    try {
        const response = await fetch('/api/v1/contracts');
        if (response.ok) {
            contracts = await response.json();
            populateContractDropdowns();
        }
    } catch (error) {
        console.error('Error loading contracts:', error);
        showNotification('Fehler beim Laden der Verträge', 'error');
    }
}

/**
 * Load all service categories
 */
async function loadServiceCategories() {
    try {
        const response = await fetch('/api/v1/service-categories');
        if (!response.ok) {
            // Categories might not exist yet, create default endpoint
            return;
        }
        serviceCategories = await response.json();
        populateServiceCategoryDropdown();
    } catch (error) {
        console.error('Error loading service categories:', error);
    }
}

/**
 * Load all service records
 */
async function loadServiceRecords() {
    try {
        const response = await fetch('/api/v1/service-records');
        if (response.ok) {
            serviceRecords = await response.json();
            displayServiceRecords();
            updateStatistics();
        } else {
            showNotification('Fehler beim Laden der Leistungen', 'error');
        }
    } catch (error) {
        console.error('Error loading service records:', error);
        showNotification('Fehler beim Laden der Leistungen', 'error');
    }
}

/**
 * Display service records in table
 */
function displayServiceRecords() {
    const tbody = document.getElementById('serviceRecordsTableBody');
    
    if (serviceRecords.length === 0) {
        tbody.innerHTML = '<tr><td colspan="10" class="text-center">Keine Leistungen erfasst</td></tr>';
        return;
    }
    
    tbody.innerHTML = serviceRecords.map(record => {
        const contract = contracts.find(c => c.id === record.contractId);
        const contractTitle = contract ? contract.title : 'Unbekannt';
        const statusBadge = getStatusBadge(record.status);
        
        return `
            <tr>
                <td>${formatDate(record.serviceDate)}</td>
                <td>${contractTitle}</td>
                <td>${record.serviceCategory || '-'}</td>
                <td>${record.description}</td>
                <td>${formatNumber(record.quantity)}</td>
                <td>${record.unit}</td>
                <td>${formatCurrency(record.unitPriceNet)}</td>
                <td>${formatCurrency(record.totalNet)}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="action-buttons">
                        ${record.status === 'DRAFT' ? `
                            <button class="btn-icon" onclick="editServiceRecord(${record.id})" title="Bearbeiten">✏️</button>
                            <button class="btn-icon" onclick="approveServiceRecord(${record.id})" title="Freigeben">✅</button>
                            <button class="btn-icon" onclick="deleteServiceRecord(${record.id})" title="Löschen">🗑️</button>
                        ` : ''}
                        ${record.status === 'APPROVED' && !record.invoiceItemId ? `
                            <button class="btn-icon" onclick="viewServiceRecord(${record.id})" title="Ansehen">👁️</button>
                        ` : ''}
                        ${record.status === 'INVOICED' ? `
                            <button class="btn-icon" onclick="viewServiceRecord(${record.id})" title="Ansehen">👁️</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Get status badge HTML
 */
function getStatusBadge(status) {
    const badges = {
        'DRAFT': '<span class="badge badge-warning">Entwurf</span>',
        'APPROVED': '<span class="badge badge-success">Freigegeben</span>',
        'INVOICED': '<span class="badge badge-info">Abgerechnet</span>',
        'REJECTED': '<span class="badge badge-danger">Abgelehnt</span>'
    };
    return badges[status] || status;
}

/**
 * Update statistics
 */
function updateStatistics() {
    const total = serviceRecords.length;
    const draft = serviceRecords.filter(r => r.status === 'DRAFT').length;
    const approved = serviceRecords.filter(r => r.status === 'APPROVED').length;
    const uninvoicedAmount = serviceRecords
        .filter(r => r.status === 'APPROVED' && !r.invoiceItemId)
        .reduce((sum, r) => sum + parseFloat(r.totalNet), 0);
    
    document.getElementById('statTotal').textContent = total;
    document.getElementById('statDraft').textContent = draft;
    document.getElementById('statApproved').textContent = approved;
    document.getElementById('statUninvoiced').textContent = formatCurrency(uninvoicedAmount);
}

/**
 * Populate contract dropdowns
 */
function populateContractDropdowns() {
    const contractSelect = document.getElementById('contractId');
    const filterContract = document.getElementById('filterContract');
    
    const options = contracts.map(c => 
        `<option value="${c.id}">${c.title} (${c.partnerName})</option>`
    ).join('');
    
    contractSelect.innerHTML = '<option value="">Bitte wählen...</option>' + options;
    filterContract.innerHTML = '<option value="">Alle Verträge</option>' + options;
}

/**
 * Populate service category dropdown
 */
function populateServiceCategoryDropdown() {
    const select = document.getElementById('serviceCategoryId');
    
    const options = serviceCategories.map(c => 
        `<option value="${c.id}">${c.name} (${c.defaultUnit})</option>`
    ).join('');
    
    select.innerHTML = '<option value="">Bitte wählen...</option>' + options;
}

/**
 * Open create modal
 */
function openCreateModal() {
    document.getElementById('modalTitle').textContent = 'Neue Leistung erfassen';
    document.getElementById('serviceRecordForm').reset();
    document.getElementById('recordId').value = '';
    document.getElementById('serviceDate').valueAsDate = new Date();
    document.getElementById('serviceRecordModal').style.display = 'flex';
}

/**
 * Close modal
 */
function closeModal() {
    document.getElementById('serviceRecordModal').style.display = 'none';
}

/**
 * Calculate total
 */
function calculateTotal() {
    const quantity = parseFloat(document.getElementById('quantity').value) || 0;
    const unitPrice = parseFloat(document.getElementById('unitPriceNet').value) || 0;
    const total = quantity * unitPrice;
    document.getElementById('totalNet').value = total.toFixed(2);
}

/**
 * Save service record
 */
async function saveServiceRecord() {
    const form = document.getElementById('serviceRecordForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const recordId = document.getElementById('recordId').value;
    const data = {
        contractId: parseInt(document.getElementById('contractId').value),
        serviceDate: document.getElementById('serviceDate').value,
        serviceCategoryId: document.getElementById('serviceCategoryId').value || null,
        servicePeriodStart: document.getElementById('servicePeriodStart').value || null,
        servicePeriodEnd: document.getElementById('servicePeriodEnd').value || null,
        description: document.getElementById('description').value,
        quantity: parseFloat(document.getElementById('quantity').value),
        unit: document.getElementById('unit').value,
        unitPriceNet: parseFloat(document.getElementById('unitPriceNet').value),
        totalNet: parseFloat(document.getElementById('totalNet').value),
        notes: document.getElementById('notes').value || null,
        createdByUserId: currentUserId,
        status: 'DRAFT'
    };
    
    try {
        const url = recordId ? `/api/v1/service-records/${recordId}` : '/api/v1/service-records';
        const method = recordId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            showNotification('Leistung erfolgreich gespeichert', 'success');
            closeModal();
            loadServiceRecords();
        } else {
            showNotification('Fehler beim Speichern der Leistung', 'error');
        }
    } catch (error) {
        console.error('Error saving service record:', error);
        showNotification('Fehler beim Speichern der Leistung', 'error');
    }
}

/**
 * Edit service record
 */
async function editServiceRecord(id) {
    const record = serviceRecords.find(r => r.id === id);
    if (!record) return;
    
    document.getElementById('modalTitle').textContent = 'Leistung bearbeiten';
    document.getElementById('recordId').value = record.id;
    document.getElementById('contractId').value = record.contractId;
    document.getElementById('serviceDate').value = record.serviceDate;
    document.getElementById('serviceCategoryId').value = record.serviceCategoryId || '';
    document.getElementById('servicePeriodStart').value = record.servicePeriodStart || '';
    document.getElementById('servicePeriodEnd').value = record.servicePeriodEnd || '';
    document.getElementById('description').value = record.description;
    document.getElementById('quantity').value = record.quantity;
    document.getElementById('unit').value = record.unit;
    document.getElementById('unitPriceNet').value = record.unitPriceNet;
    document.getElementById('totalNet').value = record.totalNet;
    document.getElementById('notes').value = record.notes || '';
    
    document.getElementById('serviceRecordModal').style.display = 'flex';
}

/**
 * Approve service record
 */
async function approveServiceRecord(id) {
    if (!confirm('Möchten Sie diese Leistung wirklich freigeben?')) return;
    
    try {
        const response = await fetch(`/api/v1/service-records/${id}/approve?approvedByUserId=${currentUserId}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Leistung erfolgreich freigegeben', 'success');
            loadServiceRecords();
        } else {
            showNotification('Fehler beim Freigeben der Leistung', 'error');
        }
    } catch (error) {
        console.error('Error approving service record:', error);
        showNotification('Fehler beim Freigeben der Leistung', 'error');
    }
}

/**
 * Delete service record
 */
async function deleteServiceRecord(id) {
    if (!confirm('Möchten Sie diese Leistung wirklich löschen?')) return;
    
    try {
        const response = await fetch(`/api/v1/service-records/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('Leistung erfolgreich gelöscht', 'success');
            loadServiceRecords();
        } else {
            showNotification('Fehler beim Löschen der Leistung', 'error');
        }
    } catch (error) {
        console.error('Error deleting service record:', error);
        showNotification('Fehler beim Löschen der Leistung', 'error');
    }
}

/**
 * Filter service records
 */
function filterServiceRecords() {
    // TODO: Implement filtering
    loadServiceRecords();
}

/**
 * Format date
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE');
}

/**
 * Format number
 */
function formatNumber(value) {
    return parseFloat(value).toFixed(2);
}

/**
 * Format currency
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('de-DE', {
        style: 'currency',
        currency: 'EUR'
    }).format(value);
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    // TODO: Implement proper notification system
    alert(message);
}
