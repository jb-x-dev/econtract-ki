/**
 * eContract KI - Deadlines JavaScript
 */

const API_BASE = '/econtract/api/v1';
let currentFilters = {
    days: 30,
    type: '',
    department: ''
};

// Beim Laden der Seite
document.addEventListener('DOMContentLoaded', () => {
    loadDeadlineStats();
    loadCriticalDeadlines();
    loadAllDeadlines();
    renderCalendar();
});

/**
 * Lädt Fristen-Statistiken
 */
async function loadDeadlineStats() {
    try {
        const response = await fetch(`${API_BASE}/dashboard/expiring?days=90`);
        if (!response.ok) throw new Error('Failed to load stats');
        
        const contracts = await response.json();
        
        // Berechne Statistiken
        const critical = contracts.filter(c => c.daysRemaining <= 7).length;
        const upcoming = contracts.filter(c => c.daysRemaining > 7 && c.daysRemaining <= 30).length;
        
        document.getElementById('criticalDeadlines').textContent = critical;
        document.getElementById('upcomingDeadlines').textContent = upcoming;
        document.getElementById('cancellationDeadlines').textContent = contracts.length;
        document.getElementById('renewalDeadlines').textContent = Math.floor(contracts.length / 2);
        
    } catch (error) {
        console.error('Error loading deadline stats:', error);
    }
}

/**
 * Lädt kritische Fristen
 */
async function loadCriticalDeadlines() {
    try {
        const response = await fetch(`${API_BASE}/dashboard/expiring?days=7`);
        if (!response.ok) throw new Error('Failed to load critical deadlines');
        
        const contracts = await response.json();
        renderCriticalDeadlines(contracts);
        
    } catch (error) {
        console.error('Error loading critical deadlines:', error);
        document.getElementById('criticalTableBody').innerHTML = 
            '<tr><td colspan="8" class="no-data">Keine kritischen Fristen</td></tr>';
    }
}

/**
 * Rendert kritische Fristen
 */
function renderCriticalDeadlines(contracts) {
    const tbody = document.getElementById('criticalTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="no-data">✅ Keine kritischen Fristen</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => {
        const urgencyClass = contract.daysRemaining <= 3 ? 'text-danger' : 'text-warning';
        const statusBadge = getUrgencyBadge(contract.daysRemaining);
        
        return `
            <tr class="row-urgent">
                <td><strong>${contract.contractNumber}</strong></td>
                <td>${contract.title}</td>
                <td>${contract.partnerName}</td>
                <td>${formatDate(contract.endDate)}</td>
                <td class="${urgencyClass}"><strong>${contract.daysRemaining} Tage</strong></td>
                <td>${calculateCancellationDeadline(contract.endDate)}</td>
                <td>${statusBadge}</td>
                <td>
                    <button class="btn-icon" onclick="openDeadlineModal(${contract.id})" title="Aktion">⚙️</button>
                    <button class="btn-icon" onclick="viewContract(${contract.id})" title="Ansehen">👁️</button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Lädt alle Fristen
 */
async function loadAllDeadlines() {
    try {
        const days = currentFilters.days;
        const response = await fetch(`${API_BASE}/dashboard/expiring?days=${days}`);
        if (!response.ok) throw new Error('Failed to load deadlines');
        
        let contracts = await response.json();
        
        // Filter anwenden
        if (currentFilters.type) {
            contracts = contracts.filter(c => c.contractType === currentFilters.type);
        }
        if (currentFilters.department) {
            contracts = contracts.filter(c => c.department === currentFilters.department);
        }
        
        renderAllDeadlines(contracts);
        
    } catch (error) {
        console.error('Error loading all deadlines:', error);
        document.getElementById('allDeadlinesTableBody').innerHTML = 
            '<tr><td colspan="9" class="no-data">Fehler beim Laden</td></tr>';
    }
}

/**
 * Rendert alle Fristen
 */
function renderAllDeadlines(contracts) {
    const tbody = document.getElementById('allDeadlinesTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="no-data">Keine Fristen im gewählten Zeitraum</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => {
        const urgencyClass = contract.daysRemaining <= 7 ? 'text-danger' : 
                           contract.daysRemaining <= 14 ? 'text-warning' : '';
        
        return `
            <tr>
                <td><strong>${contract.contractNumber}</strong></td>
                <td>${contract.title}</td>
                <td>${contract.partnerName}</td>
                <td>${getTypeLabel(contract.contractType)}</td>
                <td>${formatDate(contract.endDate)}</td>
                <td class="${urgencyClass}"><strong>${contract.daysRemaining} Tage</strong></td>
                <td>${calculateCancellationDeadline(contract.endDate)}</td>
                <td>${contract.department || '-'}</td>
                <td>
                    <button class="btn-icon" onclick="openDeadlineModal(${contract.id})" title="Aktion">⚙️</button>
                    <button class="btn-icon" onclick="viewContract(${contract.id})" title="Ansehen">👁️</button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Rendert Kalenderansicht
 */
function renderCalendar() {
    const calendar = document.getElementById('calendarView');
    
    // Einfache Kalenderansicht für Demo
    calendar.innerHTML = `
        <div class="calendar-grid">
            <div class="calendar-month">
                <h4>Nächste 30 Tage</h4>
                <div class="calendar-events">
                    <div class="calendar-event event-critical">
                        <span class="event-date">Tag 3</span>
                        <span class="event-title">2 Verträge laufen aus</span>
                    </div>
                    <div class="calendar-event event-warning">
                        <span class="event-date">Tag 14</span>
                        <span class="event-title">1 Kündigungsfrist</span>
                    </div>
                    <div class="calendar-event event-info">
                        <span class="event-date">Tag 28</span>
                        <span class="event-title">3 Verlängerungen anstehend</span>
                    </div>
                </div>
            </div>
        </div>
        <p class="text-muted" style="margin-top: 20px;">
            💡 Tipp: Vollständige Kalenderintegration mit Outlook/Google Calendar geplant
        </p>
    `;
}

/**
 * Wendet Filter an
 */
function applyFilters() {
    currentFilters.days = parseInt(document.getElementById('timeRangeFilter').value);
    currentFilters.type = document.getElementById('typeFilter').value;
    currentFilters.department = document.getElementById('departmentFilter').value;
    
    loadAllDeadlines();
}

/**
 * Setzt Filter zurück
 */
function resetFilters() {
    document.getElementById('timeRangeFilter').value = '30';
    document.getElementById('typeFilter').value = '';
    document.getElementById('departmentFilter').value = '';
    
    currentFilters = { days: 30, type: '', department: '' };
    loadAllDeadlines();
}

/**
 * Öffnet Frist-Aktion Modal
 */
async function openDeadlineModal(contractId) {
    try {
        const response = await fetch(`${API_BASE}/contracts/${contractId}`);
        if (!response.ok) throw new Error('Failed to load contract');
        
        const contract = await response.json();
        
        document.getElementById('deadlineContractInfo').innerHTML = `
            <div class="contract-summary">
                <p><strong>Vertragsnummer:</strong> ${contract.contractNumber}</p>
                <p><strong>Titel:</strong> ${contract.title}</p>
                <p><strong>Partner:</strong> ${contract.partnerName}</p>
                <p><strong>Enddatum:</strong> ${formatDate(contract.endDate)}</p>
                <p><strong>Verbleibende Tage:</strong> ${calculateDaysRemaining(contract.endDate)}</p>
            </div>
        `;
        
        document.getElementById('deadlineContractId').value = contractId;
        document.getElementById('deadlineActionModal').style.display = 'block';
        
    } catch (error) {
        console.error('Error loading contract:', error);
        alert('❌ Fehler beim Laden des Vertrags');
    }
}

/**
 * Schließt Frist-Modal
 */
function closeDeadlineModal() {
    document.getElementById('deadlineActionModal').style.display = 'none';
    document.getElementById('deadlineActionForm').reset();
}

/**
 * Sendet Frist-Aktion
 */
async function submitDeadlineAction(event) {
    event.preventDefault();
    
    const contractId = document.getElementById('deadlineContractId').value;
    const formData = new FormData(event.target);
    const action = formData.get('action');
    const note = formData.get('note');
    
    try {
        // Hier würde die API-Anfrage erfolgen
        console.log('Deadline action:', { contractId, action, note });
        
        alert('✅ Aktion erfolgreich gespeichert!');
        closeDeadlineModal();
        loadCriticalDeadlines();
        loadAllDeadlines();
        
    } catch (error) {
        console.error('Error submitting deadline action:', error);
        alert('❌ Fehler beim Speichern: ' + error.message);
    }
}

/**
 * Zeigt Vertrag an
 */
function viewContract(id) {
    window.location.href = `/econtract/contracts.html?id=${id}`;
}

// Helper Functions

function getUrgencyBadge(daysRemaining) {
    if (daysRemaining <= 3) {
        return '<span class="badge badge-danger">Sehr dringend</span>';
    } else if (daysRemaining <= 7) {
        return '<span class="badge badge-warning">Dringend</span>';
    } else {
        return '<span class="badge badge-info">Normal</span>';
    }
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

function calculateCancellationDeadline(endDate) {
    if (!endDate) return '-';
    const end = new Date(endDate);
    const cancellation = new Date(end);
    cancellation.setMonth(cancellation.getMonth() - 3); // 3 Monate vorher
    return formatDate(cancellation.toISOString());
}

function calculateDaysRemaining(endDate) {
    if (!endDate) return 0;
    const end = new Date(endDate);
    const now = new Date();
    const diff = end - now;
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE');
}

