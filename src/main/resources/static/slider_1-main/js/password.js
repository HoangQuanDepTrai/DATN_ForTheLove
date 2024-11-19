 document.getElementById("togglePassword").addEventListener("click", function() {
        const passwordInput = document.getElementById("password");
        const eyeIcon = document.getElementById("eyeIcon");
        
        if (passwordInput.type === "password") {
            passwordInput.type = "text"; // Hiển thị mật khẩu
            eyeIcon.textContent = "🙈"; // Biểu tượng khỉ nhắm mắt
        } else {
            passwordInput.type = "password"; // Ẩn mật khẩu
            eyeIcon.textContent = "🐵"; // Biểu tượng khỉ mở mắt
        }
    });
document.getElementById("toggleConfirmPassword").addEventListener("click", function() {
    const passwordInput = document.getElementById("confirm_password");
    const eyeIcon = document.getElementById("eyeIconConfirm"); // Đảm bảo sử dụng ID đúng cho biểu tượng

    if (passwordInput.type === "password") { // Thay đổi điều kiện để kiểm tra loại trường
        passwordInput.type = "text"; // Hiển thị mật khẩu
        eyeIcon.textContent = "🙈"; // Biểu tượng khỉ nhắm mắt
    } else {
        passwordInput.type = "password"; // Ẩn mật khẩu
        eyeIcon.textContent = "🐵"; // Biểu tượng khỉ mở mắt
    }
});