$(document).ready(function () {
    // Пример AJAX запроса к вашему контроллеру REST
    function loadUserInfo() {
        $.ajax({
            url: '/api/user',
            type: 'GET',
            dataType: 'json',
            success: function (data) {

                console.log(data);
                $('#userId').text(data.id);
                $('#userFirstName').text(data.firstName);
                $('#userLastName').text(data.lastName);
                $('#userAge').text(data.age);
                $('#userEmail').text(data.email);
                $('#userRole').text(data.roles.join(', '));
            },
            error: function (error) {
                console.log("Error loading user info:", error);
            }
        });
    }
    loadUserInfo();
});