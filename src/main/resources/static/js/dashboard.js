/**
 * eContract KI - Dashboard JavaScript
 */

const API_BASE = '/econtract/api/v1';
let typeChart = null;
let statusChart = null;

// Beim Laden der Seite
document.addEventListener('DOMContentLoaded', () => {
    loadDashboardStats();
    loadExpiringContracts();
});

/**
 * Lädt Dashboard-Statistiken
 */
async function loadDashboardStats() {
    try {
        const response = await fetch(`${API_BASE}/dashboard/stats`);
        if (!response.ok) throw new Error('Failed to load stats');
        
        const stats = await response.json();
        
        // Basis-Statistiken aktualisieren
        document.getElementById('totalContracts').textContent = stats.total_contracts || 0;
        document.getElementById('draftContracts').textContent = stats.draft || 0;
        document.getElementById('approvalContracts').textContent = stats.in_approval || 0;
        document.getElementById('activeContracts').textContent = stats.active || 0;
        document.getElementById('expiringContracts').textContent = stats.expiring_soon || 0;
        
        // Charts erstellen
        createTypeChart(stats.by_type);
        createStatusChart(stats);
        
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
    }
}

/**
 * Erstellt Typ-Chart
 */
function createTypeChart(byType) {
    const ctx = document.getElementById('typeChart');
    
    if (typeChart) {
        typeChart.destroy();
    }
    
    const data = {
        labels: ['Lieferant', 'Kunde', 'Dienstleistung', 'NDA'],
        datasets: [{
            data: [
                byType.SUPPLIER || 0,
                byType.CUSTOMER || 0,
                byType.SERVICE || 0,
                byType.NDA || 0
            ],
            backgroundColor: [
                'rgba(37, 99, 235, 0.8)',
                'rgba(16, 185, 129, 0.8)',
                'rgba(245, 158, 11, 0.8)',
                'rgba(139, 92, 246, 0.8)'
            ],
            borderWidth: 0
        }]
    };
    
    typeChart = new Chart(ctx, {
        type: 'doughnut',
        data: data,
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

/**
 * Erstellt Status-Chart
 */
function createStatusChart(stats) {
    const ctx = document.getElementById('statusChart');
    
    if (statusChart) {
        statusChart.destroy();
    }
    
    const data = {
        labels: ['Entwurf', 'In Genehmigung', 'Aktiv', 'Abgelaufen'],
        datasets: [{
            label: 'Anzahl Verträge',
            data: [
                stats.draft || 0,
                stats.in_approval || 0,
                stats.active || 0,
                stats.expired || 0
            ],
            backgroundColor: [
                'rgba(59, 130, 246, 0.8)',
                'rgba(245, 158, 11, 0.8)',
                'rgba(16, 185, 129, 0.8)',
                'rgba(239, 68, 68, 0.8)'
            ],
            borderWidth: 0
        }]
    };
    
    statusChart = new Chart(ctx, {
        type: 'bar',
        data: data,
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

/**
 * Lädt ablaufende Verträge
 */
async function loadExpiringContracts() {
    try {
        const response = await fetch(`${API_BASE}/dashboard/expiring?days=30`);
        if (!response.ok) throw new Error('Failed to load expiring contracts');
        
        const contracts = await response.json();
        renderExpiringContracts(contracts);
        
    } catch (error) {
        console.error('Error loading expiring contracts:', error);
        document.getElementById('expiringTableBody').innerHTML = 
            '<tr><td colspan="6" class="no-data">Fehler beim Laden</td></tr>';
    }
}

/**
 * Rendert ablaufende Verträge
 */
function renderExpiringContracts(contracts) {
    const tbody = document.getElementById('expiringTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="no-data">Keine ablaufenden Verträge</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => {
        const daysClass = contract.daysRemaining <= 7 ? 'text-danger' : 
                         contract.daysRemaining <= 14 ? 'text-warning' : '';
        
        return `
            <tr>
                <td><strong>${contract.contractNumber}</strong></td>
                <td>${contract.title}</td>
                <td>${contract.partnerName}</td>
                <td>${formatDate(contract.endDate)}</td>
                <td class="${daysClass}"><strong>${contract.daysRemaining} Tage</strong></td>
                <td>
                    <button class="btn-icon" onclick="viewContract(${contract.id})" title="Ansehen">👁️</button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Zeigt Vertrag an
 */
function viewContract(id) {
    window.location.href = `/econtract/contract-detail.html?id=${id}`;
}

/**
 * Formatiert Datum
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE');
}

