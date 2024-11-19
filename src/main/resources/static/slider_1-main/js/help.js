// Biến toàn cục để lưu trữ đối tượng nhận diện giọng nói
var recognition;

// Mở modal
function openModal() {
    const modal = document.getElementById('voiceModal');
    modal.classList.add('show'); // Thêm class để hiển thị
    startVoiceRecognition(); // Bắt đầu nhận diện giọng nói
}

// Đóng modal
function closeModal() {
    const modal = document.getElementById('voiceModal');
    modal.classList.remove('show'); // Xóa class để ẩn
    if (recognition) {
        recognition.stop(); // Dừng nhận diện giọng nói khi đóng modal
        recognition = null; // Đặt lại recognition về null
    }
}

// Nhận diện giọng nói
function startVoiceRecognition() {
    if ('webkitSpeechRecognition' in window) {
        recognition = new webkitSpeechRecognition(); // Khởi tạo đối tượng nhận diện giọng nói
        recognition.lang = 'vi-VN'; 
        recognition.interimResults = false;
        recognition.maxAlternatives = 1;

        recognition.onstart = function() {
            console.log('Bắt đầu nhận diện giọng nói...');
        };

        recognition.onresult = function(event) {
            var transcript = event.results[0][0].transcript;
            document.getElementById('input').value = transcript; // Điền kết quả giọng nói vào ô tìm kiếm
            console.log('Kết quả giọng nói: ', transcript);
            closeModal(); // Đóng modal sau khi nhận diện giọng nói
            document.getElementById('searchForm').submit(); // Gửi form để tìm kiếm
        };

        recognition.onerror = function(event) {
            console.error('Lỗi nhận diện giọng nói: ', event.error);
            alert('Đã xảy ra lỗi khi nhận diện giọng nói. Vui lòng thử lại.');
        };

        recognition.onend = function() {
            console.log('Kết thúc nhận diện giọng nói.');
        };

        recognition.start(); // Bắt đầu nhận diện giọng nói
    } else {
        alert('Trình duyệt của bạn không hỗ trợ nhận diện giọng nói.');
    }
}
function confirmLogout() {
    Swal.fire({
        title: 'Xác nhận đăng xuất',
        text: "Bạn có chắc chắn muốn đăng xuất không?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Đăng xuất',
        cancelButtonText: 'Hủy'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = '/logout'; // Chuyển hướng tới trang đăng xuất
        }
    });
}