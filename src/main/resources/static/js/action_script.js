let tbody = document.getElementById("actionTableBody");
let totalPages;
let currentPage = 1;
let sortByAttribute = null;
let sortDirection = 'DESC';

let myInput = document.getElementById("myInput");


getTotalPages().then(totalPage => {
    totalPages = Math.floor(totalPage);
    console.log(totalPages);
    updatePagination(currentPage);
}).catch(error => {
    console.error('Error:', error);
});

function getTotalPages() {
    return fetch('http://localhost:8080/api/v1/getCountControls')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            totalPages = parseInt(data.toString()) / 20;
            return totalPages;
        })
        .catch(error => {
            console.error('Error fetching total pages:', error);
            throw error; // Re-throw lỗi để cho phép code gọi getTotalPages() xử lý lỗi nếu cần
        });
}
function updatePagination(currentPage) {
    const paginationContainer = document.querySelector('.pagination');
    paginationContainer.innerHTML = '';

    const pageLinks = [];

    // Thêm nút "Previous"
    pageLinks.push(`<a href="#" id="prevPage" ${currentPage === 1 ? 'style="display: none;"' : ''}>&laquo;</a>`);

    // Tính toán các trang được hiển thị trước và sau trang hiện tại
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages , startPage + 4);


    // Thêm các trang
    for (let i = startPage; i <= endPage; i++) {
        pageLinks.push(`<a href="#" class="pageNumber ${i === currentPage ? 'active' : ''}">${i}</a>`);
    }


    // Thêm nút "Next"
    pageLinks.push(`<a href="#" id="nextPage" ${currentPage === totalPages ? 'style="display: none;"' : ''}>&raquo;</a>`);

    // Thêm các nút vào container pagination
    paginationContainer.innerHTML = pageLinks.join('');
}

function checkSearch(){
    if (myInput.value !== null || myInput !== undefined || myInput !== ' '){
        searchImpl();
    }else {
        filldata(currentPage,sortByAttribute,sortDirection);
    }
}

// Thêm sự kiện click cho các nút phân trang
document.querySelector('.pagination').addEventListener('click', function(e) {
    if (e.target.classList.contains('pageNumber')) {
        const pageNumber = parseInt(e.target.textContent);
        currentPage = pageNumber;
        updatePagination(currentPage);
        // Gọi hàm filldata với trang hiện tại
        checkSearch();
    } else if (e.target.id === 'prevPage') {
        currentPage = Math.max(1, currentPage - 1);
        updatePagination(currentPage);
        // Gọi hàm filldata với trang hiện tại
        checkSearch();
    } else if (e.target.id === 'nextPage') {
        currentPage = Math.min(totalPages, currentPage + 1);
        updatePagination(currentPage);
        checkSearch();
    }
});

const tableHeaders = document.querySelectorAll('thead th');
// Thêm sự kiện "click" cho mỗi cột
tableHeaders.forEach(header => {
    header.addEventListener('click', function() {
        if (myInput.value !== null || myInput.value !== ' ' || myInput.value !== undefined){
            let keyword = myInput.value;
            sortByAttribute = this.getAttribute('data-sortby');
            sortDirection = sortDirection === 'ASC' ? 'DESC' : 'ASC';
            searchData(keyword,currentPage,sortByAttribute,sortDirection);
        }
        else {
            // Lấy giá trị sortby từ thuộc tính data-sortby của thẻ th
            sortByAttribute = this.getAttribute('data-sortby');

            // Cập nhật giá trị sortBy
            sortBy = sortByAttribute;

            // Cập nhật giá trị sortDirection
            sortDirection = sortDirection === 'ASC' ? 'DESC' : 'ASC';

            // Gọi lại hàm để lấy dữ liệu mới từ máy chủ
            filldata(currentPage,sortByAttribute,sortDirection);
        }
    });
});
// Function để xóa tất cả các hàng trên bảng
function clearTable() {
    tbody.innerHTML = ""; // Xóa tất cả các phần tử con của tbody
}
async function fetchDataAndFillTable(api) {
    try {
        // Fetch dữ liệu từ API
        const response = await fetch(api);

        // Kiểm tra trạng thái của response
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        // Chuyển response sang JSON
        const data = await response.json();
        clearTable();
        data.forEach(control => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${control.id}</td>
                <td>${formatDate(control.date)}</td>
                <td>${control.statusFan ? 'on' : 'off'}</td>
                <td>${control.statusLight ? 'on' : 'off'}</td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching sensor data:', error);
    }
}
function formatDate(datestr) {
    const date = new Date(datestr);
    const seconds = date.getSeconds();
    const minutes = date.getMinutes();
    const hours = date.getHours();
    const day = date.getDate();
    const month = date.getMonth() + 1; // Tháng bắt đầu từ 0 nên cần cộng thêm 1
    const year = date.getFullYear();
    const datefm =   year + '-' + month + '-' + day + ' ' +seconds + ':' + minutes + ':' + hours;
    return datefm;
}
function filldata(pagenum,sortBy,sortDirection){
    let api = 'http://localhost:8080/api/v1/getdatacontrol';
    let params = new URLSearchParams();
    if (pagenum >= 1){pagenum -= 1;}
    if (pagenum !== null ) params.append('pagenum', pagenum );
    params.append('pagesize', 20);
    if (sortBy !== null ) params.append('sortBy',sortBy);
    if (sortDirection !== null ) params.append('sortDirection',sortDirection);
    fetchDataAndFillTable(api + '?' + params.toString());
}



function searchData(keyword,pagenum,sortBy,sortDirection){
    let api = 'http://localhost:8080/api/v1/searchControl';
    const params = new URLSearchParams();
    currentPage = pagenum;
    if (pagenum >= 1) pagenum -=1;
    if (pagenum !== null ) params.append('pagenum', pagenum );
    params.append('pagesize', 20);
    if (keyword !== null) params.append('keyword',keyword);
    if (sortBy !== null ) params.append('sortBy',sortBy);
    if (sortDirection !== null ) params.append('sortDirection',sortDirection);
    fetchDataAndFillTable(api + '?' + params.toString());
    updatePagination(currentPage);
}
function searchImpl(){
    if (myInput.value !== null || myInput.value !== ' ' || myInput.value !== undefined) {
        let keyword = myInput.value; 
        searchData(keyword,currentPage,sortByAttribute,sortDirection);
    }
}

myInput.addEventListener("input", function() {
    const keyword = myInput.value; // Lấy giá trị nhập vào từ ô input và loại bỏ khoảng trắng đầu cuối
    let page = 1;
    // Gọi hàm để lấy dữ liệu từ API với các tham số đã chọn
    searchData(keyword,page,'id',"DESC");
});

// Gọi hàm để thực hiện fetch và điền dữ liệu vào bảng
window.addEventListener('load',filldata(1,null,null));
