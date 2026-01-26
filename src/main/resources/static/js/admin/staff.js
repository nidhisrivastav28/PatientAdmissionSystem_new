/******************************** Switch Tabs ********************************/

const tabs = document.querySelectorAll('.tab');

tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => {
            t.classList.remove('text-blue-600', 'border-blue-500', 'font-medium');
            t.classList.add('text-gray-600', ' border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none');
        });

        tab.classList.add('text-blue-600', 'border-blue-500', 'font-medium');
        tab.classList.remove('text-gray-600', ' border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none');

        const role = tab.dataset.role;
        loadStaff(role);
    });
});

/******************************** Open/Close Modal ********************************/

function openModal() {
    document.getElementById('staffModal').classList.remove('hidden');
    document.getElementById('staffModal').classList.add('flex'); // For centering
}

function closeModal() {
    document.getElementById('staffModal').classList.add('hidden');
    document.getElementById('staffForm').reset();
    document.getElementById('roleFields').innerHTML = '';
    document.getElementById('roleFields').classList.add('hidden');
    document.getElementById('finalSubmitDiv').classList.add('hidden');
}

function generateShiftOptions() {
    let options = '';
    shiftValues.forEach(shift => {
        options += `<option value="${shift}">${shift}</option>`;
    });
    return options;
}

function selectRole(role) {
    document.getElementById('role').value = role; // send role to backend
    const roleFields = document.getElementById('roleFields');
    roleFields.classList.remove('hidden');
    document.getElementById('finalSubmitDiv').classList.remove('hidden');

    let fields = '';

    switch (role) {
        case 'DOCTOR':
            fields = `
                <label class="block text-sm font-medium text-gray-700">Specialization</label>
                <input type="text" id="specialization" name="specialisation" class="mt-1 block w-full px-3 border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none shadow-sm" required />
                <label class="block text-sm font-medium text-gray-700 mt-2">License Number</label>
                <input type="text" id="license" name="licenseNumber" class="mt-1 block w-full px-3 border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none shadow-sm" required />
        </div>
                `;
            break;
        case 'NURSE':
            fields = `
                <label class="block text-sm font-medium text-gray-700">Department</label>
                <input type="text" id="department" name="department" class="mt-1 block w-full px-3 border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none shadow-sm" required />
				<label class="block text-sm font-medium text-gray-700 mt-2">Shift</label>
				<select id="shift" name="shift" class="mt-1 block w-full px-3 border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none shadow-sm">
				    ${generateShiftOptions()}
				</select>
            `;
            break;
        case 'RECEPTIONIST':
            fields = `
                <label class="block text-sm font-medium text-gray-700">Desk</label>
                <input type="text" id="desk" name="desk" class="mt-1 block w-full px-3 border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none shadow-sm" required />
				<label class="block text-sm font-medium text-gray-700 mt-2">Shift</label>
				<select id="shift" name="shift" class="mt-1 block w-full px-3 border-0 border-b-2 border-gray-400 focus:border-blue-600 focus:outline-none shadow-sm">
					${generateShiftOptions()}
				</select>
            `;
            break;
    }

    roleFields.innerHTML = fields;
}