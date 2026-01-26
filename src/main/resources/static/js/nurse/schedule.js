/******************************** Open/Close Modal ********************************/

function openModal() {
    document.getElementById('scheduleModal').classList.remove('hidden');
    document.getElementById('scheduleModal').classList.add('flex'); // For centering
}

function closeModal() {
    document.getElementById('scheduleModal').classList.add('hidden');
    document.getElementById('scheduleForm').reset();
    document.getElementById('finalSubmitDiv').classList.add('hidden');
}

/******************************** Handle Appointment Creation ********************************/

const nId = document.getElementById('n_id');
const nName = document.getElementById('n_name');
const pId = document.getElementById('p_id');
const pName = document.getElementById('pname');
const dateInput = document.getElementById('date');
const timeSelect = document.getElementById('time');
const taskInput = document.getElementById('task');
const submitBtn = document.getElementById('submitBtn');

function updateTimeOptions() {
    timeSelect.innerHTML = '';
    if (!nId.value || !dateInput.value) return;

    const today = new Date().toISOString().split('T')[0];
    if (dateInput.value < today) {
        timeSelect.disabled = true;
        submitBtn.disabled = true;
        return;
    } else timeSelect.disabled = false;

    let startHour = 0, startMinute = 0;

    for (let h = startHour; h < 24; h++) {
        const timeStr = `${h.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}`;
        const option = document.createElement('option');
        option.value = timeStr;
        option.text = timeStr;
        timeSelect.appendChild(option);
        startMinute = 0;
    }

    timeSelect.value = '';
    checkSubmit();
}

function updateNurseName() {
    const nurse = nurses.find(n => n.ID == nId.value);
    nName.value = nurse ? nurse.name : '';
    updateTimeOptions();
}

function updatePatientName() {
    const patient = patients.find(p => p.ID == pId.value);
    pName.value = patient ? patient.name : '';
    checkSubmit();
}

function checkSubmit() {
	
    submitBtn.disabled = !(nId.value && nName.value && pId.value && pName.value && dateInput.value && timeSelect.value && taskInput.value);
}

// Event listeners
nId.addEventListener('change', updateNurseName);
pId.addEventListener('change', updatePatientName);
dateInput.addEventListener('change', updateTimeOptions);
taskInput.addEventListener('input', checkSubmit);