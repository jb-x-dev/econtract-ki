/**
 * eContract KI - AI Assistant JavaScript
 */

const API_BASE = '/econtract/api/v1';
let generatedContractData = null;

/**
 * Wechselt zwischen Tabs
 */
function switchTab(tabName) {
    // Alle Tabs deaktivieren
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // Aktiven Tab aktivieren
    event.target.classList.add('active');
    document.getElementById(tabName + 'Tab').classList.add('active');
}

/**
 * Generiert Vertrag mit KI
 */
async function generateContract(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    
    const parameters = {
        contractType: formData.get('contractType'),
        partnerName: formData.get('partnerName'),
        startDate: formData.get('startDate') || null,
        endDate: formData.get('endDate') || null,
        contractValue: formData.get('contractValue') ? parseFloat(formData.get('contractValue')) : null,
        department: formData.get('department') || null,
        additionalRequirements: formData.get('additionalRequirements') || null
    };
    
    // Speichere für später
    generatedContractData = parameters;
    
    try {
        showLoading('Vertrag wird generiert...');
        
        const response = await fetch(`${API_BASE}/ai/generate-contract`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(parameters)
        });
        
        if (!response.ok) throw new Error('Generation failed');
        
        const result = await response.json();
        
        // Ergebnis anzeigen
        document.getElementById('generatedContract').innerHTML = 
            '<pre>' + escapeHtml(result.content) + '</pre>';
        document.getElementById('generateResult').style.display = 'block';
        
        hideLoading();
        
    } catch (error) {
        console.error('Error generating contract:', error);
        hideLoading();
        alert('❌ Fehler bei der Vertragsgenerierung: ' + error.message);
    }
}

/**
 * Analysiert Vertragsrisiken
 */
async function analyzeContract(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    const contractText = formData.get('contractText');
    
    try {
        showLoading('Vertrag wird analysiert...');
        
        const response = await fetch(`${API_BASE}/ai/analyze-risks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ contractText })
        });
        
        if (!response.ok) throw new Error('Analysis failed');
        
        const result = await response.json();
        
        // Ergebnis anzeigen
        let html = '<div class="risk-analysis">';
        html += '<div class="risk-level risk-' + (result.risk_level || 'unknown').toLowerCase() + '">';
        html += '<strong>Risikostufe:</strong> ' + (result.risk_level || 'UNBEKANNT');
        html += '</div>';
        
        if (result.analysis) {
            html += '<div class="analysis-text">';
            html += '<h5>Analyse:</h5>';
            html += '<p>' + escapeHtml(result.analysis) + '</p>';
            html += '</div>';
        }
        
        if (result.confidence) {
            html += '<p><strong>Konfidenz:</strong> ' + (result.confidence * 100).toFixed(0) + '%</p>';
        }
        
        html += '</div>';
        
        document.getElementById('riskAnalysis').innerHTML = html;
        document.getElementById('analyzeResult').style.display = 'block';
        
        hideLoading();
        
    } catch (error) {
        console.error('Error analyzing contract:', error);
        hideLoading();
        alert('❌ Fehler bei der Risikoanalyse: ' + error.message);
    }
}

/**
 * Schlägt Klauseln vor
 */
async function suggestClauses(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    const contractType = formData.get('contractType');
    const context = formData.get('context') || '';
    
    try {
        showLoading('Klauseln werden vorgeschlagen...');
        
        const url = `${API_BASE}/ai/suggest-clauses?contractType=${contractType}&context=${encodeURIComponent(context)}`;
        const response = await fetch(url);
        
        if (!response.ok) throw new Error('Suggestion failed');
        
        const clauses = await response.json();
        
        // Ergebnis anzeigen
        const list = document.getElementById('clausesList');
        list.innerHTML = clauses.map(clause => 
            '<li class="clause-item">' + escapeHtml(clause) + '</li>'
        ).join('');
        
        document.getElementById('clausesResult').style.display = 'block';
        
        hideLoading();
        
    } catch (error) {
        console.error('Error suggesting clauses:', error);
        hideLoading();
        alert('❌ Fehler beim Vorschlagen der Klauseln: ' + error.message);
    }
}

/**
 * Speichert generierten Vertrag
 */
async function saveGeneratedContract() {
    if (!generatedContractData) {
        alert('Keine Vertragsdaten vorhanden');
        return;
    }
    
    const contractText = document.getElementById('generatedContract').textContent;
    
    const contractDTO = {
        title: `${generatedContractData.contractType} mit ${generatedContractData.partnerName}`,
        contractType: generatedContractData.contractType,
        partnerName: generatedContractData.partnerName,
        startDate: generatedContractData.startDate,
        endDate: generatedContractData.endDate,
        contractValue: generatedContractData.contractValue,
        currency: 'EUR',
        department: generatedContractData.department,
        ownerUserId: 1,
        createdBy: 1
    };
    
    try {
        const response = await fetch(`${API_BASE}/contracts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(contractDTO)
        });
        
        if (!response.ok) throw new Error('Save failed');
        
        const created = await response.json();
        
        alert('✅ Vertrag erfolgreich gespeichert!');
        window.location.href = `/econtract/contracts.html`;
        
    } catch (error) {
        console.error('Error saving contract:', error);
        alert('❌ Fehler beim Speichern: ' + error.message);
    }
}

/**
 * Kopiert Text in Zwischenablage
 */
function copyToClipboard() {
    const text = document.getElementById('generatedContract').textContent;
    navigator.clipboard.writeText(text).then(() => {
        alert('✅ In Zwischenablage kopiert!');
    }).catch(err => {
        console.error('Error copying:', err);
        alert('❌ Fehler beim Kopieren');
    });
}

/**
 * Zeigt Loading-Indicator
 */
function showLoading(message) {
    // TODO: Implement loading overlay
    console.log('Loading:', message);
}

/**
 * Versteckt Loading-Indicator
 */
function hideLoading() {
    // TODO: Implement loading overlay
    console.log('Loading complete');
}

/**
 * Escaped HTML für sichere Anzeige
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

