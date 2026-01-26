/******************************** Open/Close Modal ********************************/

function openModal() {
    document.getElementById('appointmentModal').classList.remove('hidden');
    document.getElementById('appointmentModal').classList.add('flex'); // For centering
}

function closeModal() {
    document.getElementById('appointmentModal').classList.add('hidden');
    document.getElementById('appointmentForm').reset();
    document.getElementById('finalSubmitDiv').classList.add('hidden');
}

/******************************** Handle Appointment Creation ********************************/

const dId = document.getElementById('d_id');
const dName = document.getElementById('d_name');
const pId = document.getElementById('p_id');
const pName = document.getElementById('pname');
const dateInput = document.getElementById('date');
const fromSelect = document.getElementById('from');
const toInput = document.getElementById('to');
const reasonInput = document.getElementById('reason');
const submitBtn = document.getElementById('submitBtn');

function updateFromOptions() {
    fromSelect.innerHTML = '';
    toInput.value = '';
    if (!dId.value || !dateInput.value) return;

    const today = new Date().toISOString().split('T')[0];
    if (dateInput.value < today) {
        fromSelect.disabled = true;
        toInput.value = '--';
        submitBtn.disabled = true;
        return;
    } else fromSelect.disabled = false;

    let startHour = 9, startMinute = 0;
    const doctorTimes = lastEndTimes[dId.value];
    if (doctorTimes && doctorTimes[dateInput.value]) {
        const [h, m] = doctorTimes[dateInput.value].split(':');
        startHour = parseInt(h);
        startMinute = parseInt(m);
    }

    for (let h = startHour; h < 18; h++) {
        const timeStr = `${h.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}`;
        const option = document.createElement('option');
        option.value = timeStr;
        option.text = timeStr;
        fromSelect.appendChild(option);
        startMinute = 0;
    }

    fromSelect.value = '';
    toInput.value = '';
    checkSubmit();
}


function updateToTime() {
    if (!fromSelect.value) { toInput.value = ''; checkSubmit(); return; }
    const [h, m] = fromSelect.value.split(':').map(Number);
    const date = new Date(); date.setHours(h, m + 50);
    toInput.value = `${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`;
    checkSubmit();
}

function updateDoctorName() {
    const doctor = doctors.find(d => d.ID == dId.value);
    dName.value = doctor ? doctor.name : '';
    updateFromOptions();
}

function updatePatientName() {
    const patient = patients.find(p => p.ID == pId.value);
    pName.value = patient ? patient.name : '';
    checkSubmit();
}

function checkSubmit() {

    submitBtn.disabled = !(dId.value && dName.value && pId.value && pName.value && dateInput.value && fromSelect.value && toInput.value && reasonInput.value);
}

// Event listeners
dId.addEventListener('change', updateDoctorName);
pId.addEventListener('change', updatePatientName);
dateInput.addEventListener('change', updateFromOptions);
fromSelect.addEventListener('change', updateToTime);
reasonInput.addEventListener('input', checkSubmit);