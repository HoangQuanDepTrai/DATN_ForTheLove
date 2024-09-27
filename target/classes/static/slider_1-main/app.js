


/*// Select the modal and the iframe
const myModal = document.getElementById('exampleModal');
const youtubeVideo = document.getElementById('youtubeVideo');

// Function to update the video URL to autoplay when modal is shown
myModal.addEventListener('show.bs.modal', function () {
    const videoSrc = youtubeVideo.src;
    youtubeVideo.src = videoSrc + "&autoplay=1";
});

// Function to reset the video URL when modal is hidden (stops the video)
myModal.addEventListener('hide.bs.modal', function () {
    youtubeVideo.src = youtubeVideo.src.replace("&autoplay=1", "");
});*/

/*
 // Khi modal mở
    $('#exampleModal').on('show.bs.modal', function (e) {
        // Lấy iframe và tạo lại src để bắt đầu phát video
        var movieTrailerUrl = '${movie.trailerUrl}'; // Lấy URL trailer từ model
        var iframe = $('#youtubeVideo');
        iframe.attr('src', 'https://www.youtube.com/embed/' + movieTrailerUrl + '?autoplay=1'); // Đặt src để tự động phát video
    });

    // Khi modal đóng
    $('#exampleModal').on('hide.bs.modal', function (e) {
        // Dừng video bằng cách đặt src về một URL không có video
        var iframe = $('#youtubeVideo');
        iframe.attr('src', ''); // Xóa src để dừng video
    });*/


//step 1: get DOM
let nextDom = document.getElementById('next');
let prevDom = document.getElementById('prev');

let carouselDom = document.querySelector('.carousel');
let SliderDom = carouselDom.querySelector('.carousel .list');
let thumbnailBorderDom = document.querySelector('.carousel .thumbnail');
let thumbnailItemsDom = thumbnailBorderDom.querySelectorAll('.item');
let timeDom = document.querySelector('.carousel .time');

thumbnailBorderDom.appendChild(thumbnailItemsDom[0]);
let timeRunning = 3000;
let timeAutoNext = 7000;

nextDom.onclick = function () {
    showSlider('next');
}

prevDom.onclick = function () {
    showSlider('prev');
}
let runTimeOut;
let runNextAuto = setTimeout(() => {
    next.click();
}, timeAutoNext)
function showSlider(type) {
    let SliderItemsDom = SliderDom.querySelectorAll('.carousel .list .item');
    let thumbnailItemsDom = document.querySelectorAll('.carousel .thumbnail .item');

    if (type === 'next') {
        SliderDom.appendChild(SliderItemsDom[0]);
        thumbnailBorderDom.appendChild(thumbnailItemsDom[0]);
        carouselDom.classList.add('next');
    } else {
        SliderDom.prepend(SliderItemsDom[SliderItemsDom.length - 1]);
        thumbnailBorderDom.prepend(thumbnailItemsDom[thumbnailItemsDom.length - 1]);
        carouselDom.classList.add('prev');
    }
    clearTimeout(runTimeOut);
    runTimeOut = setTimeout(() => {
        carouselDom.classList.remove('next');
        carouselDom.classList.remove('prev');
    }, timeRunning);

    clearTimeout(runNextAuto);
    runNextAuto = setTimeout(() => {
        next.click();
    }, timeAutoNext)
}


// ========================================== SLIDER CARD
// ========================================== SLIDER CARD
// ========================================== Khung big
document.addEventListener("DOMContentLoaded", function () {
    const carousel = document.querySelector(".carousel-khung-big");
    const arrowBtns = document.querySelectorAll(".wrapper i");
    const wrapper = document.querySelector(".wrapper");

    const firstCard = carousel.querySelector(".card-sliders");
    const firstCardWidth = firstCard.offsetWidth;

    let isDragging = false,
        startX,
        startScrollLeft,
        timeoutId;

    const dragStart = (e) => {
        isDragging = true;
        carousel.classList.add("dragging");
        startX = e.pageX;
        startScrollLeft = carousel.scrollLeft;
    };

    const dragging = (e) => {
        if (!isDragging) return;

        // Calculate the new scroll position
        const newScrollLeft = startScrollLeft - (e.pageX - startX);

        // Check if the new scroll position exceeds 
        // the carousel boundaries
        if (newScrollLeft <= 0 || newScrollLeft >=
            carousel.scrollWidth - carousel.offsetWidth) {

            // If so, prevent further dragging
            isDragging = false;
            return;
        }

        // Otherwise, update the scroll position of the carousel
        carousel.scrollLeft = newScrollLeft;
    };

    const dragStop = () => {
        isDragging = false;
        carousel.classList.remove("dragging");
    };

    const autoPlay = () => {

        // Return if window is smaller than 800
        if (window.innerWidth < 800) return;

        // Calculate the total width of all cards
        const totalCardWidth = carousel.scrollWidth;

        // Calculate the maximum scroll position
        const maxScrollLeft = totalCardWidth - carousel.offsetWidth;

        // If the carousel is at the end, stop autoplay
        if (carousel.scrollLeft >= maxScrollLeft) return;

        // Autoplay the carousel after every 2500ms
        timeoutId = setTimeout(() =>
            carousel.scrollLeft += firstCardWidth, 2500);
    };

    carousel.addEventListener("mousedown", dragStart);
    carousel.addEventListener("mousemove", dragging);
    document.addEventListener("mouseup", dragStop);
    wrapper.addEventListener("mouseenter", () =>
        clearTimeout(timeoutId));
    wrapper.addEventListener("mouseleave", autoPlay);

    // Add event listeners for the arrow buttons to 
    // scroll the carousel left and right
    arrowBtns.forEach(btn => {
        btn.addEventListener("click", () => {
            carousel.scrollLeft += btn.id === "left" ?
                -firstCardWidth : firstCardWidth;
        });
    });
});
// ========================================== Khung big
// ========================================== movie_card
// document.addEventListener("DOMContentLoaded", function () {
//     // Lấy tất cả các thẻ movie_card
//     const cards = document.querySelectorAll(".movie_card");
//     const loadMoreButton = document.getElementById("xem-them-card");
//     let visibleCards = 5; // Số thẻ hiển thị ban đầu

//     // Ẩn các thẻ sau 5 thẻ đầu tiên
//     for (let i = visibleCards; i < cards.length; i++) {
//         cards[i].style.display = "none"; // Dùng display: none
//     }

//     // Sự kiện khi nhấn nút Load More
//     loadMoreButton.addEventListener("click", function () {
//         // Hiển thị thêm 5 thẻ nữa
//         let nextVisibleCards = visibleCards + 5;
//         for (let i = visibleCards; i < nextVisibleCards && i < cards.length; i++) {
//             cards[i].style.display = "block"; // Hiển thị thẻ mới
//         }

//         visibleCards = nextVisibleCards;

//         // Ẩn nút Load More nếu đã hiển thị hết thẻ
//         if (visibleCards >= cards.length) {
//             loadMoreButton.style.display = "none";
//         }
//     });
// });

document.addEventListener('DOMContentLoaded', () => {
    let loadMoreBtn = document.querySelector('#load-more');
    let currentItem = 8; // Number of boxes to display initially
    let loadMoreAmount = 4; // Number of boxes to display on each button click

    // Function to display boxes
    const displayBoxes = (start, count) => {
        let boxes = [...document.querySelectorAll('.container .box-container .box')];
        for (let i = start; i < start + count; i++) {
            if (boxes[i]) { // Check if the box exists
                boxes[i].style.display = 'inline-block';
            }
        }
    }

    // Display 6 boxes initially
    displayBoxes(0, currentItem);

    loadMoreBtn.onclick = () => {
        let boxes = [...document.querySelectorAll('.container .box-container .box')];
        displayBoxes(currentItem, loadMoreAmount); // Show 3 more boxes
        currentItem += loadMoreAmount; // Increment the number of displayed boxes

        if (currentItem >= boxes.length) {
            loadMoreBtn.style.display = 'none'; // Hide button if no more boxes
        }
    }
});
