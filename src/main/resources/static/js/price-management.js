/**
 * Price Management
 * eContract KI - Billing Module
 */

let prices = [];
let contracts = [];
let serviceCategories = [];

document.addEventListener('DOMContentLoaded', function() {
    loadContracts();
    loadServiceCategories();
    loadPrices();
    
    document.getElementById('validFrom').valueAsDate = new Date();
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

async function loadServiceCategories() {
    try {
        const response = await fetch('/api/v1/service-categories');
        if (response.ok) {
            serviceCategories = await response.json();
            populateServiceCategoryDropdown();
        }
    } catch (error) {
        console.error('Error loading service categories:', error);
    }
}

async function loadPrices() {
    const contractId = document.getElementById('filterContract').value;
    const active = document.getElementById('filterActive').value;
    
    try {
        let url = '/api/v1/pricing/contract-prices';
        if (contractId) {
            url = `/api/v1/pricing/contracts/${contractId}/prices${active === 'true' ? '/active' : ''}`;
        }
        
        const response = await fetch(url);
        if (response.ok) {
            prices = await response.json();
            
            if (active && !contractId) {
                prices = prices.filter(p => p.isActive === (active === 'true'));
            }
            
            displayPrices();
            updateStatistics();
        }
    } catch (error) {
        console.error('Error loading prices:', error);
        showNotification('Fehler beim Laden der Preise', 'error');
    }
}

function displayPrices() {
    const tbody = document.getElementById('pricesTableBody');
    
    if (prices.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">Keine Preise vorhanden</td></tr>';
        return;
    }
    
    tbody.innerHTML = prices.map(price => {
        const contract = contracts.find(c => c.id === price.contractId);
        const contractTitle = contract ? contract.title : 'Unbekannt';
        const statusBadge = price.isActive ? 
            '<span class="badge badge-success">Aktiv</span>' : 
            '<span class="badge badge-secondary">Inaktiv</span>';
        
        return `
            <tr>
                <td>${contractTitle}</td>
                <td>${price.serviceCategory || '-'}</td>
                <td>${price.description}</td>
                <td>${price.unit}</td>
                <td>${formatCurrency(price.unitPriceNet)}</td>
                <td>${formatDate(price.validFrom)}</td>
                <td>${price.validTo ? formatDate(price.validTo) : 'Unbegrenzt'}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon" onclick="editPrice(${price.id})" title="Bearbeiten">✏️</button>
                        <button class="btn-icon" onclick="manageTiers(${price.id})" title="Staffelpreise">📊</button>
                        <button class="btn-icon" onclick="deletePrice(${price.id})" title="Löschen">🗑️</button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function updateStatistics() {
    const total = prices.length;
    const active = prices.filter(p => p.isActive).length;
    
    document.getElementById('statTotal').textContent = total;
    document.getElementById('statActive').textContent = active;
    document.getElementById('statWithTiers').textContent = '0'; // TODO: Load tier counts
}

function populateContractDropdowns() {
    const contractSelect = document.getElementById('priceContractId');
    const filterContract = document.getElementById('filterContract');
    
    const options = contracts.map(c => 
        `<option value="${c.id}">${c.title} (${c.partnerName})</option>`
    ).join('');
    
    contractSelect.innerHTML = '<option value="">Bitte wählen...</option>' + options;
    filterContract.innerHTML = '<option value="">Alle Verträge</option>' + options;
}

function populateServiceCategoryDropdown() {
    const select = document.getElementById('serviceCategoryId');
    
    const options = serviceCategories.map(c => 
        `<option value="${c.id}">${c.name}</option>`
    ).join('');
    
    select.innerHTML = '<option value="">Bitte wählen...</option>' + options;
}

function openCreatePriceModal() {
    document.getElementById('priceModalTitle').textContent = 'Neuer Preis';
    document.getElementById('priceForm').reset();
    document.getElementById('priceId').value = '';
    document.getElementById('validFrom').valueAsDate = new Date();
    document.getElementById('isActive').checked = true;
    document.getElementById('priceModal').style.display = 'flex';
}

function closePriceModal() {
    document.getElementById('priceModal').style.display = 'none';
}

async function savePrice() {
    const form = document.getElementById('priceForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const priceId = document.getElementById('priceId').value;
    const data = {
        contractId: parseInt(document.getElementById('priceContractId').value),
        serviceCategoryId: document.getElementById('serviceCategoryId').value || null,
        description: document.getElementById('priceDescription').value,
        unit: document.getElementById('priceUnit').value,
        unitPriceNet: parseFloat(document.getElementById('unitPriceNet').value),
        validFrom: document.getElementById('validFrom').value,
        validTo: document.getElementById('validTo').value || null,
        isActive: document.getElementById('isActive').checked
    };
    
    try {
        const url = priceId ? `/api/v1/pricing/contract-prices/${priceId}` : '/api/v1/pricing/contract-prices';
        const method = priceId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            showNotification('Preis erfolgreich gespeichert', 'success');
            closePriceModal();
            loadPrices();
        } else {
            showNotification('Fehler beim Speichern des Preises', 'error');
        }
    } catch (error) {
        console.error('Error saving price:', error);
        showNotification('Fehler beim Speichern des Preises', 'error');
    }
}

async function editPrice(id) {
    const price = prices.find(p => p.id === id);
    if (!price) return;
    
    document.getElementById('priceModalTitle').textContent = 'Preis bearbeiten';
    document.getElementById('priceId').value = price.id;
    document.getElementById('priceContractId').value = price.contractId;
    document.getElementById('serviceCategoryId').value = price.serviceCategoryId || '';
    document.getElementById('priceDescription').value = price.description;
    document.getElementById('priceUnit').value = price.unit;
    document.getElementById('unitPriceNet').value = price.unitPriceNet;
    document.getElementById('validFrom').value = price.validFrom;
    document.getElementById('validTo').value = price.validTo || '';
    document.getElementById('isActive').checked = price.isActive;
    
    document.getElementById('priceModal').style.display = 'flex';
}

async function deletePrice(id) {
    if (!confirm('Möchten Sie diesen Preis wirklich löschen?')) return;
    
    try {
        const response = await fetch(`/api/v1/pricing/contract-prices/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('Preis erfolgreich gelöscht', 'success');
            loadPrices();
        } else {
            showNotification('Fehler beim Löschen des Preises', 'error');
        }
    } catch (error) {
        console.error('Error deleting price:', error);
        showNotification('Fehler beim Löschen des Preises', 'error');
    }
}

function manageTiers(priceId) {
    // TODO: Implement tier management modal
    alert('Staffelpreis-Verwaltung wird in Kürze verfügbar sein.');
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
