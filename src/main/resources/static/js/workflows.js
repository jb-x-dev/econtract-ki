/**
 * eContract KI - Workflows JavaScript
 */

const API_BASE = '/econtract/api/v1';
let currentContractId = null;

// Beim Laden der Seite
document.addEventListener('DOMContentLoaded', () => {
    loadWorkflowStats();
    loadMyApprovals();
    loadAllWorkflows();
});

/**
 * Lädt Workflow-Statistiken
 */
async function loadWorkflowStats() {
    // Mock-Daten für Demo
    document.getElementById('pendingApprovals').textContent = '0';
    document.getElementById('inProgressWorkflows').textContent = '0';
    document.getElementById('approvedToday').textContent = '0';
    document.getElementById('overdueApprovals').textContent = '0';
}

/**
 * Lädt meine offenen Genehmigungen
 */
async function loadMyApprovals() {
    try {
        // Lade alle Verträge im Status IN_APPROVAL
        const response = await fetch(`${API_BASE}/contracts?status=IN_APPROVAL`);
        if (!response.ok) throw new Error('Failed to load approvals');
        
        const data = await response.json();
        const contracts = data.content || [];
        
        renderMyApprovals(contracts);
        
    } catch (error) {
        console.error('Error loading my approvals:', error);
        document.getElementById('myApprovalsTableBody').innerHTML = 
            '<tr><td colspan="7" class="no-data">Keine offenen Genehmigungen</td></tr>';
    }
}

/**
 * Rendert meine Genehmigungen
 */
function renderMyApprovals(contracts) {
    const tbody = document.getElementById('myApprovalsTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="no-data">Keine offenen Genehmigungen</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => `
        <tr>
            <td><strong>${contract.contractNumber}</strong></td>
            <td>${contract.title}</td>
            <td>${contract.partnerName}</td>
            <td>${formatCurrency(contract.contractValue)} ${contract.currency}</td>
            <td><span class="badge badge-warning">Fachabteilung</span></td>
            <td>${calculateDeadline()}</td>
            <td>
                <button class="btn-icon" onclick="openApprovalModal(${contract.id})" title="Genehmigen/Ablehnen">✅</button>
                <button class="btn-icon" onclick="viewWorkflow(${contract.id})" title="Workflow ansehen">👁️</button>
            </td>
        </tr>
    `).join('');
}

/**
 * Lädt alle Workflows
 */
async function loadAllWorkflows() {
    try {
        const response = await fetch(`${API_BASE}/contracts`);
        if (!response.ok) throw new Error('Failed to load workflows');
        
        const data = await response.json();
        const contracts = data.content || [];
        
        renderAllWorkflows(contracts);
        
    } catch (error) {
        console.error('Error loading workflows:', error);
        document.getElementById('allWorkflowsTableBody').innerHTML = 
            '<tr><td colspan="7" class="no-data">Fehler beim Laden</td></tr>';
    }
}

/**
 * Rendert alle Workflows
 */
function renderAllWorkflows(contracts) {
    const tbody = document.getElementById('allWorkflowsTableBody');
    
    if (contracts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="no-data">Keine Workflows vorhanden</td></tr>';
        return;
    }
    
    tbody.innerHTML = contracts.map(contract => {
        const statusBadge = getStatusBadge(contract.status);
        const progress = calculateProgress(contract.status);
        
        return `
            <tr>
                <td><strong>${contract.contractNumber}</strong></td>
                <td>${contract.title}</td>
                <td>${formatDate(contract.createdAt)}</td>
                <td>${getCurrentStep(contract.status)}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${progress}%"></div>
                    </div>
                    <span class="progress-text">${progress}%</span>
                </td>
                <td>
                    <button class="btn-icon" onclick="viewWorkflow(${contract.id})" title="Details">👁️</button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Öffnet Genehmigungsmodal
 */
async function openApprovalModal(contractId) {
    currentContractId = contractId;
    
    try {
        const response = await fetch(`${API_BASE}/contracts/${contractId}`);
        if (!response.ok) throw new Error('Failed to load contract');
        
        const contract = await response.json();
        
        document.getElementById('contractInfo').innerHTML = `
            <div class="contract-summary">
                <p><strong>Vertragsnummer:</strong> ${contract.contractNumber}</p>
                <p><strong>Titel:</strong> ${contract.title}</p>
                <p><strong>Partner:</strong> ${contract.partnerName}</p>
                <p><strong>Wert:</strong> ${formatCurrency(contract.contractValue)} ${contract.currency}</p>
                <p><strong>Laufzeit:</strong> ${formatDate(contract.startDate)} - ${formatDate(contract.endDate)}</p>
            </div>
        `;
        
        document.getElementById('approvalContractId').value = contractId;
        document.getElementById('approvalModal').style.display = 'block';
        
    } catch (error) {
        console.error('Error loading contract:', error);
        alert('❌ Fehler beim Laden des Vertrags');
    }
}

/**
 * Schließt Genehmigungsmodal
 */
function closeApprovalModal() {
    document.getElementById('approvalModal').style.display = 'none';
    document.getElementById('approvalForm').reset();
    currentContractId = null;
}

/**
 * Setzt Genehmigungsaktion
 */
function setApprovalAction(action) {
    document.getElementById('approvalAction').value = action;
    document.getElementById('approvalForm').dispatchEvent(new Event('submit'));
}

/**
 * Sendet Genehmigung
 */
async function submitApproval(event) {
    event.preventDefault();
    
    const contractId = document.getElementById('approvalContractId').value;
    const action = document.getElementById('approvalAction').value;
    const comment = event.target.comment.value;
    
    if (!action) {
        alert('Bitte wählen Sie eine Aktion (Genehmigen/Ablehnen)');
        return;
    }
    
    try {
        const endpoint = action === 'approve' ? 'approve' : 'reject';
        const response = await fetch(`${API_BASE}/workflows/${contractId}/${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                approverId: 1,
                comment: comment
            })
        });
        
        if (!response.ok) throw new Error('Approval failed');
        
        const actionText = action === 'approve' ? 'genehmigt' : 'abgelehnt';
        alert(`✅ Vertrag wurde ${actionText}!`);
        
        closeApprovalModal();
        loadMyApprovals();
        loadAllWorkflows();
        
    } catch (error) {
        console.error('Error submitting approval:', error);
        alert('❌ Fehler bei der Genehmigung: ' + error.message);
    }
}

/**
 * Zeigt Workflow-Details
 */
async function viewWorkflow(contractId) {
    try {
        const response = await fetch(`${API_BASE}/contracts/${contractId}`);
        if (!response.ok) throw new Error('Failed to load workflow');
        
        const contract = await response.json();
        
        // Workflow-Timeline anzeigen
        const timeline = document.getElementById('workflowTimeline');
        timeline.innerHTML = generateWorkflowTimeline(contract);
        
        document.getElementById('workflowDetailCard').style.display = 'block';
        document.getElementById('workflowDetailCard').scrollIntoView({ behavior: 'smooth' });
        
    } catch (error) {
        console.error('Error viewing workflow:', error);
        alert('❌ Fehler beim Laden des Workflows');
    }
}

/**
 * Generiert Workflow-Timeline
 */
function generateWorkflowTimeline(contract) {
    const steps = [
        { name: 'Entwurf erstellt', status: 'completed', date: contract.createdAt },
        { name: 'Fachabteilung', status: contract.status === 'DRAFT' ? 'pending' : 'completed', date: null },
        { name: 'Rechtsabteilung', status: 'pending', date: null },
        { name: 'Finanzabteilung', status: 'pending', date: null },
        { name: 'Geschäftsführung', status: 'pending', date: null }
    ];
    
    return steps.map(step => `
        <div class="workflow-step ${step.status}">
            <h4>${step.name}</h4>
            <p class="step-status">${getStepStatusText(step.status)}</p>
            ${step.date ? `<p class="step-date">${formatDate(step.date)}</p>` : ''}
        </div>
    `).join('');
}

/**
 * Sucht Workflows
 */
function searchWorkflows() {
    const query = document.getElementById('searchInput').value;
    // TODO: Implement search
    console.log('Search:', query);
}

/**
 * Wendet Filter an
 */
function applyFilters() {
    const status = document.getElementById('statusFilter').value;
    // TODO: Implement filtering
    console.log('Filter:', status);
}

/**
 * Behandelt Suche bei Enter
 */
function handleSearch(event) {
    if (event.key === 'Enter') {
        searchWorkflows();
    }
}

// Helper Functions

function getStatusBadge(status) {
    const badges = {
        'DRAFT': '<span class="badge badge-secondary">Entwurf</span>',
        'IN_APPROVAL': '<span class="badge badge-warning">In Genehmigung</span>',
        'ACTIVE': '<span class="badge badge-success">Aktiv</span>',
        'EXPIRED': '<span class="badge badge-danger">Abgelaufen</span>'
    };
    return badges[status] || '<span class="badge">Unbekannt</span>';
}

function getCurrentStep(status) {
    const steps = {
        'DRAFT': 'Entwurf',
        'IN_APPROVAL': 'Fachabteilung',
        'ACTIVE': 'Abgeschlossen',
        'EXPIRED': 'Abgelaufen'
    };
    return steps[status] || 'Unbekannt';
}

function calculateProgress(status) {
    const progress = {
        'DRAFT': 20,
        'IN_APPROVAL': 50,
        'ACTIVE': 100,
        'EXPIRED': 100
    };
    return progress[status] || 0;
}

function getStepStatusText(status) {
    const texts = {
        'completed': '✅ Abgeschlossen',
        'pending': '⏳ Ausstehend',
        'rejected': '❌ Abgelehnt'
    };
    return texts[status] || 'Unbekannt';
}

function calculateDeadline() {
    const days = Math.floor(Math.random() * 5) + 1;
    return `${days} Tage`;
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

