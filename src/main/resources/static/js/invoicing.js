/**
 * Invoicing Management
 * eContract KI - Billing Module
 */

let invoices = [];
let contracts = [];
let uninvoicedServiceRecords = [];
let selectedServiceRecordIds = [];
let currentUserId = 1;

document.addEventListener('DOMContentLoaded', function() {
    loadContracts();
    loadInvoices();
});

async function loadContracts() {
    try {
        const response = await fetch('/api/v1/contracts');
        if (response.ok) {
            contracts = await response.json();
            populateContractDropdowns();
        }
    } catch (error) {
        console.error('Error loading contracts:', error);
    }
}

async function loadInvoices() {
    const contractId = document.getElementById('filterContract').value;
    const status = document.getElementById('filterStatus').value;
    
    try {
        let url = '/api/v1/invoices';
        if (contractId) {
            url = `/api/v1/invoices/contract/${contractId}`;
        } else if (status) {
            url = `/api/v1/invoices/status/${status}`;
        }
        
        const response = await fetch(url);
        if (response.ok) {
            invoices = await response.json();
            displayInvoices();
            updateStatistics();
        }
    } catch (error) {
        console.error('Error loading invoices:', error);
        showNotification('Fehler beim Laden der Rechnungen', 'error');
    }
}

function displayInvoices() {
    const tbody = document.getElementById('invoicesTableBody');
    
    if (invoices.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">Keine Rechnungen vorhanden</td></tr>';
        return;
    }
    
    tbody.innerHTML = invoices.map(invoice => {
        const statusBadge = getStatusBadge(invoice.status);
        const period = invoice.billingPeriodStart && invoice.billingPeriodEnd ?
            `${formatDate(invoice.billingPeriodStart)} - ${formatDate(invoice.billingPeriodEnd)}` : '-';
        
        return `
            <tr>
                <td><strong>${invoice.invoiceNumber}</strong></td>
                <td>${formatDate(invoice.invoiceDate)}</td>
                <td>${invoice.partnerName || '-'}</td>
                <td>${period}</td>
                <td>${formatCurrency(invoice.subtotalNet)}</td>
                <td>${formatCurrency(invoice.totalGross)}</td>
                <td>${formatDate(invoice.dueDate)}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="action-buttons">
                        ${invoice.status === 'DRAFT' ? `
                            <button class="btn-icon" onclick="viewInvoice(${invoice.id})" title="Ansehen">👁️</button>
                            <button class="btn-icon" onclick="approveInvoice(${invoice.id})" title="Freigeben">✅</button>
                            <button class="btn-icon" onclick="deleteInvoice(${invoice.id})" title="Löschen">🗑️</button>
                        ` : ''}
                        ${invoice.status === 'APPROVED' ? `
                            <button class="btn-icon" onclick="viewInvoice(${invoice.id})" title="Ansehen">👁️</button>
                            <button class="btn-icon" onclick="markAsSent(${invoice.id})" title="Als versendet markieren">📤</button>
                        ` : ''}
                        ${invoice.status === 'SENT' || invoice.status === 'OVERDUE' ? `
                            <button class="btn-icon" onclick="viewInvoice(${invoice.id})" title="Ansehen">👁️</button>
                            <button class="btn-icon" onclick="markAsPaid(${invoice.id})" title="Als bezahlt markieren">💰</button>
                        ` : ''}
                        ${invoice.status === 'PAID' || invoice.status === 'CANCELLED' ? `
                            <button class="btn-icon" onclick="viewInvoice(${invoice.id})" title="Ansehen">👁️</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function getStatusBadge(status) {
    const badges = {
        'DRAFT': '<span class="badge badge-secondary">Entwurf</span>',
        'APPROVED': '<span class="badge badge-info">Freigegeben</span>',
        'SENT': '<span class="badge badge-primary">Versendet</span>',
        'PAID': '<span class="badge badge-success">Bezahlt</span>',
        'OVERDUE': '<span class="badge badge-danger">Überfällig</span>',
        'CANCELLED': '<span class="badge badge-dark">Storniert</span>'
    };
    return badges[status] || status;
}

function updateStatistics() {
    const total = invoices.length;
    const sent = invoices.filter(i => i.status === 'SENT' || i.status === 'OVERDUE').length;
    const overdue = invoices.filter(i => i.status === 'OVERDUE').length;
    const openAmount = invoices
        .filter(i => i.status === 'SENT' || i.status === 'OVERDUE' || i.status === 'APPROVED')
        .reduce((sum, i) => sum + parseFloat(i.totalGross), 0);
    
    document.getElementById('statTotal').textContent = total;
    document.getElementById('statSent').textContent = sent;
    document.getElementById('statOverdue').textContent = overdue;
    document.getElementById('statOpenAmount').textContent = formatCurrency(openAmount);
}

function populateContractDropdowns() {
    const filterContract = document.getElementById('filterContract');
    const srContractId = document.getElementById('srContractId');
    
    const options = contracts.map(c => 
        `<option value="${c.id}">${c.title} (${c.partnerName})</option>`
    ).join('');
    
    filterContract.innerHTML = '<option value="">Alle Verträge</option>' + options;
    srContractId.innerHTML = '<option value="">Bitte wählen...</option>' + options;
}

function showCreateFromServiceRecordsModal() {
    document.getElementById('createFromServiceRecordsModal').style.display = 'flex';
    document.getElementById('uninvoicedServiceRecordsSection').style.display = 'none';
    selectedServiceRecordIds = [];
}

function closeCreateFromServiceRecordsModal() {
    document.getElementById('createFromServiceRecordsModal').style.display = 'none';
}

async function loadUninvoicedServiceRecords() {
    const contractId = document.getElementById('srContractId').value;
    if (!contractId) {
        document.getElementById('uninvoicedServiceRecordsSection').style.display = 'none';
        return;
    }
    
    try {
        const response = await fetch(`/api/v1/service-records/contract/${contractId}/uninvoiced`);
        if (response.ok) {
            uninvoicedServiceRecords = await response.json();
            displayUninvoicedServiceRecords();
            document.getElementById('uninvoicedServiceRecordsSection').style.display = 'block';
        }
    } catch (error) {
        console.error('Error loading uninvoiced service records:', error);
        showNotification('Fehler beim Laden der Leistungen', 'error');
    }
}

function displayUninvoicedServiceRecords() {
    const tbody = document.getElementById('uninvoicedServiceRecordsBody');
    
    if (uninvoicedServiceRecords.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Keine nicht abgerechneten Leistungen vorhanden</td></tr>';
        return;
    }
    
    tbody.innerHTML = uninvoicedServiceRecords.map(record => `
        <tr>
            <td><input type="checkbox" class="service-record-checkbox" value="${record.id}" onchange="updateSelectedServiceRecords()"></td>
            <td>${formatDate(record.serviceDate)}</td>
            <td>${record.description}</td>
            <td>${record.quantity} ${record.unit}</td>
            <td>${formatCurrency(record.totalNet)}</td>
        </tr>
    `).join('');
}

function toggleAllServiceRecords() {
    const selectAll = document.getElementById('selectAllServiceRecords').checked;
    const checkboxes = document.querySelectorAll('.service-record-checkbox');
    checkboxes.forEach(cb => cb.checked = selectAll);
    updateSelectedServiceRecords();
}

function updateSelectedServiceRecords() {
    const checkboxes = document.querySelectorAll('.service-record-checkbox:checked');
    selectedServiceRecordIds = Array.from(checkboxes).map(cb => parseInt(cb.value));
    
    const selectedRecords = uninvoicedServiceRecords.filter(r => selectedServiceRecordIds.includes(r.id));
    const total = selectedRecords.reduce((sum, r) => sum + parseFloat(r.totalNet), 0);
    
    document.getElementById('selectedCount').textContent = selectedServiceRecordIds.length;
    document.getElementById('selectedTotal').textContent = formatCurrency(total);
}

async function createInvoiceFromServiceRecords() {
    if (selectedServiceRecordIds.length === 0) {
        showNotification('Bitte wählen Sie mindestens eine Leistung aus', 'warning');
        return;
    }
    
    const contractId = parseInt(document.getElementById('srContractId').value);
    
    const data = {
        contractId: contractId,
        serviceRecordIds: selectedServiceRecordIds,
        createdByUserId: currentUserId
    };
    
    try {
        const response = await fetch('/api/v1/invoices/from-service-records', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            const invoice = await response.json();
            showNotification(`Rechnung ${invoice.invoiceNumber} erfolgreich erstellt`, 'success');
            closeCreateFromServiceRecordsModal();
            loadInvoices();
        } else {
            showNotification('Fehler beim Erstellen der Rechnung', 'error');
        }
    } catch (error) {
        console.error('Error creating invoice:', error);
        showNotification('Fehler beim Erstellen der Rechnung', 'error');
    }
}

async function approveInvoice(id) {
    if (!confirm('Möchten Sie diese Rechnung wirklich freigeben?')) return;
    
    try {
        const response = await fetch(`/api/v1/invoices/${id}/approve?approvedByUserId=${currentUserId}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Rechnung erfolgreich freigegeben', 'success');
            loadInvoices();
        } else {
            showNotification('Fehler beim Freigeben der Rechnung', 'error');
        }
    } catch (error) {
        console.error('Error approving invoice:', error);
        showNotification('Fehler beim Freigeben der Rechnung', 'error');
    }
}

async function markAsSent(id) {
    if (!confirm('Möchten Sie diese Rechnung als versendet markieren?')) return;
    
    try {
        const response = await fetch(`/api/v1/invoices/${id}/send?sentByUserId=${currentUserId}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Rechnung als versendet markiert', 'success');
            loadInvoices();
        } else {
            showNotification('Fehler beim Markieren der Rechnung', 'error');
        }
    } catch (error) {
        console.error('Error marking invoice as sent:', error);
        showNotification('Fehler beim Markieren der Rechnung', 'error');
    }
}

async function markAsPaid(id) {
    if (!confirm('Möchten Sie diese Rechnung als bezahlt markieren?')) return;
    
    try {
        const response = await fetch(`/api/v1/invoices/${id}/paid`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showNotification('Rechnung als bezahlt markiert', 'success');
            loadInvoices();
        } else {
            showNotification('Fehler beim Markieren der Rechnung', 'error');
        }
    } catch (error) {
        console.error('Error marking invoice as paid:', error);
        showNotification('Fehler beim Markieren der Rechnung', 'error');
    }
}

async function deleteInvoice(id) {
    if (!confirm('Möchten Sie diese Rechnung wirklich löschen?')) return;
    
    try {
        const response = await fetch(`/api/v1/invoices/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('Rechnung erfolgreich gelöscht', 'success');
            loadInvoices();
        } else {
            showNotification('Fehler beim Löschen der Rechnung', 'error');
        }
    } catch (error) {
        console.error('Error deleting invoice:', error);
        showNotification('Fehler beim Löschen der Rechnung', 'error');
    }
}

function viewInvoice(id) {
    // TODO: Implement invoice detail view
    alert('Rechnungsdetails werden in Kürze verfügbar sein.');
}

function openCreateInvoiceModal() {
    // TODO: Implement manual invoice creation
    alert('Manuelle Rechnungserstellung wird in Kürze verfügbar sein.');
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE');
}

function formatCurrency(value) {
    return new Intl.NumberFormat('de-DE', {
        style: 'currency',
        currency: 'EUR'
    }).format(value);
}

function showNotification(message, type = 'info') {
    alert(message);
}
