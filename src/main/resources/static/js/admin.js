document.addEventListener('DOMContentLoaded', () => {
    loadUsers();

    const createUserForm = document.getElementById('createUserForm');
    createUserForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(createUserForm);
        const userData = Object.fromEntries(formData);

        await fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        createUserForm.reset();
        loadUsers();
    });
});

async function loadUsers() {
    const response = await fetch('/api/users');
    const users = await response.json();
    const usersTableBody = document.getElementById('usersTableBody');
    usersTableBody.innerHTML = ''; // Очистите предыдущие данные

    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.age}</td>
            <td>${user.username}</td>
            <td>${user.roles.join(', ')}</td>
            <td><button onclick="editUser(${user.id})">Edit</button></td>
            <td><button onclick="deleteUser(${user.id})">Delete</button></td>
        `;
        usersTableBody.appendChild(row);
    });
}

async function editUser(userId) {
    const response = await fetch(`/api/users/${userId}`);
    const user = await response.json();

    // Здесь вы можете заполнить поля формы в модальном окне значениями из user
    document.getElementById(`name_edit${userId}`).value = user.firstName;
    document.getElementById(`lastname_edit${userId}`).value = user.lastName;
    document.getElementById(`age_edit${userId}`).value = user.age;
    document.getElementById(`username_edit${userId}`).value = user.username;
    // Для пароля можно оставить поле пустым, если вы не хотите его предзаполнять
    document.getElementById(`roles_edit${userId}`).value = user.roles; // Настройте как нужно
}

async function updateUser(userId) {
    const userData = {
        id: userId,
        firstName: document.getElementById(`name_edit${userId}`).value,
        lastName: document.getElementById(`lastname_edit${userId}`).value,
        age: document.getElementById(`age_edit${userId}`).value,
        username: document.getElementById(`username_edit${userId}`).value,
        password: document.getElementById(`password_edit${userId}`).value,
        roles: Array.from(document.getElementById(`roles_edit${userId}`).selectedOptions).map(option => option.value)
    };

    await fetch(`/api/users/${userId}`, {
        method: 'PUT', // Обновляем данные
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    });
    closeEditModal();
    loadUsers();
}

async function deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user?')) {
        await fetch(`/api/users/${userId}`, {
            method: 'DELETE',
        });
        loadUsers();
    }
}
