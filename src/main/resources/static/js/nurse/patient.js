/******************************** Open/Close Modal ********************************/

function openModal() {
    document.getElementById('patientModal').classList.remove('hidden');
    document.getElementById('patientModal').classList.add('flex'); // For centering
}

function closeModal() {
    document.getElementById('patientModal').classList.add('hidden');
    document.getElementById('patientForm').reset();
    document.getElementById('roleFields').innerHTML = '';
    document.getElementById('roleFields').classList.add('hidden');
    document.getElementById('finalSubmitDiv').classList.add('hidden');
}