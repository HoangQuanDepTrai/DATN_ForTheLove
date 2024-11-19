function submitBooking(button) {
    const selectedSeats = [...document.querySelectorAll('.selected')].map(seat => {
        const seatId = seat.getAttribute('data-seat-id'); // Lấy seatId
        return {
            seatId: seatId, // Lưu seatId
            seatName: seat.innerText // Hoặc bạn có thể lưu seatName
        };
    });

    // Lấy form từ nút được nhấn
    const form = button.closest('form');

    // Lấy showtimeId từ input ẩn
    const showtimeIdInput = form.querySelector('input[name="showtimeId"]');
    const showtimeId = showtimeIdInput ? showtimeIdInput.value : null; // Lấy giá trị từ input ẩn

    // Lấy thông tin sản phẩm và số lượng
    const products = [...document.querySelectorAll('.draggable-content')].map(row => {
        const quantityInput = row.querySelector('.comboQuantity');
        const quantity = parseInt(quantityInput.value);
        const productIdElement = row.querySelector('.product-id'); // Phần tử chứa ID sản phẩm

        const productId = productIdElement ? productIdElement.innerText : null; // Lấy ID sản phẩm từ h5

        return {productId, quantity};
    }).filter(product => product.quantity > 0); // Lọc sản phẩm có số lượng lớn hơn 0

    // Gán dữ liệu vào các thẻ input hidden
    document.getElementById('selectedSeats').value = JSON.stringify(selectedSeats); // Gửi thông tin ghế
    document.getElementById('products').value = JSON.stringify(products);

    // Ghi lại dữ liệu để kiểm tra
    console.log('Selected Seats:', selectedSeats);
    console.log('Products:', products);
    console.log('Showtime ID:', showtimeId); // Ghi lại showtimeId

    // Gửi form
    form.submit();
}


/*JS MODAL*/
// Lấy các phần tử sản phẩm và tổng tiền combo
const products = document.querySelectorAll('.draggable-content');
const comboTotalAmountElement = document.getElementById('comboTotalAmount1');

// Hàm cập nhật tổng số tiền của mỗi sản phẩm
function updateProductTotalAmount(productElement) {
    const quantityInput = productElement.querySelector('input.comboQuantity');
    const priceElement = productElement.querySelector('p.price');
    const totalAmountElement = productElement.querySelector('div.comboTotalAmount');

    // Xử lý trường hợp ô nhập số lượng bị trống hoặc không hợp lệ
    const quantity = parseInt(quantityInput.value, 10) || 0;
    const price = parseFloat(priceElement.dataset.price);

    if (isNaN(price)) {
        totalAmountElement.textContent = '0 VND';
        return;
    }

    const totalAmount = quantity * price; // nhân với 1000 cho đơn vị tiền
    totalAmountElement.textContent = formatPrice(totalAmount);
}

// Hàm cập nhật tổng tiền của tất cả các sản phẩm (grand total)
function updateGrandTotal() {
    let grandTotal = 0;
    products.forEach((productElement) => {
        const totalAmountElement = productElement.querySelector('div.comboTotalAmount');
        const totalAmount = parseFloat(totalAmountElement.textContent.replace(/\D/g, ''));
        if (!isNaN(totalAmount)) {
            grandTotal += totalAmount;
        }
    });
    comboTotalAmountElement.textContent = formatPrice(grandTotal);
}

// Hàm định dạng giá tiền (thêm dấu phẩy và chấm)
function formatPrice(price) {
    return price.toLocaleString('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0,
    });
}

// Hàm kiểm tra và xác thực giá trị nhập vào
function validateInput(inputElement) {
    // Lấy giá trị từ input
    let value = inputElement.value.trim();

    // Kiểm tra nếu giá trị là một số và không âm
    if (value === "" || isNaN(value) || Number(value) < 0) {
        inputElement.value = 0; // Nếu không hợp lệ, gán lại giá trị 0
    } else {
        inputElement.value = Math.max(0, Number(value)); // Gán giá trị hợp lệ (không âm)
    }

    // Cập nhật tổng tiền sản phẩm
    const productElement = inputElement.closest('.draggable-content');
    updateProductTotalAmount(productElement);
    updateGrandTotal();
}

// Thêm sự kiện cho các nút tăng và giảm số lượng
products.forEach((productElement) => {
    const quantityInput = productElement.querySelector('input.comboQuantity');
    const priceElement = productElement.querySelector('p.price');
    const btnIncrease = productElement.querySelector('.btn-increase');
    const btnDecrease = productElement.querySelector('.btn-decrease');

    // Nút tăng số lượng
    btnIncrease.addEventListener('click', () => {
        quantityInput.value = parseInt(quantityInput.value, 10) + 1;
        const price = parseFloat(priceElement.textContent.replace(' VND', '').replace('.', '').replace(',', '.')) || 0; // Lấy giá từ priceElement
        priceElement.dataset.price = price; // Cập nhật giá trị vào data-price
        updateProductTotalAmount(productElement);
        updateGrandTotal();
    });

    // Nút giảm số lượng
    btnDecrease.addEventListener('click', () => {
        let currentValue = parseInt(quantityInput.value, 10);
        if (currentValue > 0) {
            quantityInput.value = currentValue - 1;
        }
        const price = parseFloat(priceElement.textContent.replace(' VND', '').replace('.', '').replace(',', '.')) || 0; // Lấy giá từ priceElement
        priceElement.dataset.price = price; // Cập nhật giá trị vào data-price
        updateProductTotalAmount(productElement);
        updateGrandTotal();
    });

    // Sự kiện thay đổi giá trị ô input
    quantityInput.addEventListener('input', () => {
        const quantity = parseInt(quantityInput.value, 10) || 0;
        const price = parseFloat(priceElement.textContent.replace(' VND', '').replace('.', '').replace(',', '.')) || 0; // Lấy giá từ priceElement
        priceElement.dataset.price = price; // Cập nhật giá trị vào data-price
        updateProductTotalAmount(productElement);
        updateGrandTotal();
    });
});
/*JS MODAL*/


/*JS QUA HÓA ĐƠN*/
const selectedSeats = [];

function selectSeat(seat) {
    const seatName = seat.innerText; // Lấy tên ghế

    // Kiểm tra xem ghế đã được chọn chưa
    if (seat.classList.contains('selected')) {
        // Bỏ chọn ghế
        seat.classList.remove('selected');
        selectedSeats.splice(selectedSeats.indexOf(seatName), 1);
    } else {
        // Chọn ghế
        seat.classList.add('selected');
        selectedSeats.push(seatName);
    }

    // Cập nhật hiển thị ghế đã chọn và tổng số tiền
    updateSelectedSeatsDisplay();
    updateTotalAmount();
}

function updateSelectedSeatsDisplay() {
    const display = document.getElementById('selectedSeatsDisplay');
    if (selectedSeats.length === 0) {
        display.innerText = 'Chưa có ghế nào được chọn.';
    } else {
        display.innerText = selectedSeats.join(', ');
    }
}

function updateTotalAmount() {
    const totalAmountDisplay = document.getElementById('totalAmount');
    let total = 0;

    selectedSeats.forEach(seatName => {
        // Tìm phần tử ghế bằng cách so sánh tên ghế
        const seatButton = Array.from(document.getElementsByClassName('seat')).find(seat => seat.innerText === seatName);

        // Lấy giá từ thuộc tính value của ghế (được thiết lập động từ backend)
        const seatPrice = parseInt(seatButton?.value); // Sử dụng optional chaining để tránh lỗi

        // Kiểm tra xem giá ghế có hợp lệ không
        if (isNaN(seatPrice)) {
            console.error(`Giá không hợp lệ cho ghế: ${seatName}`);
            Swal.fire({
                title: "Lỗi!",
                text: "Vị trí ghế đã chọn không hợp lệ!!",
                icon: "error"
            });
            return; // Thoát hàm nếu giá không hợp lệ
        }

        // Cộng giá vào tổng
        total += seatPrice;
    });

    // Định dạng tổng số tiền với dấu chấm mỗi ba chữ số
    const formattedTotal = total.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    totalAmountDisplay.innerText = formattedTotal + ' VNĐ';
}
function confirmBooking() {
    // Kiểm tra xem người dùng đã chọn ghế chưa
    if (selectedSeats.length === 0) {
        Swal.fire({
            title: "Thông báo!",
            text: "Vui lòng chọn ghế trước khi đặt vé!",
            icon: "error"
        });
        return;  // Ngừng hàm nếu chưa chọn ghế
    }

    // Nếu tất cả đều hợp lệ, hiển thị modal đặt vé
    const comboModal = new bootstrap.Modal(document.getElementById('comboModal'));
    comboModal.show();
}

/*JS TRỞ VỀ BTN*/
document.getElementById("backButton").addEventListener("click", function () {
    window.history.back();
});
/*JS TRỞ VỀ BTN*/

/*JS WEB SOCKET*/
/*
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
    // Đăng ký nhận thông báo khi có thay đổi trạng thái ghế
    stompClient.subscribe('/topic/seatStatus', (message) => {
        const seatStatus = JSON.parse(message.body);
        updateSeatDisplay(seatStatus);
    });
});

function selectSeat(seatId) {
    // Gửi yêu cầu chọn ghế lên server
    stompClient.send("/app/seat/select", {}, JSON.stringify({ seatId: seatId, status: 'selected' }));
}

function updateSeatDisplay(seatStatus) {
    // Cập nhật giao diện ghế dựa trên trạng thái mới
    const seatElement = document.getElementById(`seat-${seatStatus.seatId}`);
    seatElement.style.backgroundColor = seatStatus.status === 'selected' ? 'red' : 'green';
}
*/

