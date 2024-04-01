
let temp_val = document.getElementById('temp_val');
let hum_val = document.getElementById('hum_val');
let lux_val = document.getElementById('lux_val');

let temp = document.querySelector('.temperature');
let hum = document.querySelector('.humidity');
let lux = document.querySelector('.light');
let status = 0;


const toggleButtonLight = document.getElementById('toggleButton_light');
const statusTextLight = document.getElementById('statusLight');
const iconoff = document.getElementById('lightoff');
const iconon = document.getElementById('lighton');

const toggleButtonFan = document.getElementById('toggleButton_fan');
const statusTextFan = document.getElementById('statusFan');
const iconfanon = document.getElementById('iconfanon');
const iconfanoff = document.getElementById('iconfanoff');


function toggleButtonLightUpdate() {
    if (toggleButtonLight.checked) {
        statusTextLight.textContent = 'On';
        iconoff.style.display = 'none';
        iconon.style.display = 'block';
        localStorage.setItem('lightStatus', toggleButtonLight.checked);
    } else {
        statusTextLight.textContent = 'Off';
        iconon.style.display = 'none';
        iconoff.style.display = 'block';
        localStorage.setItem('lightStatus', null);
    }
}

function toggleButtonFanUpdate() {
    if (toggleButtonFan.checked) {
        statusTextFan.textContent = 'On';
        iconfanoff.style.display = 'none';
        iconfanon.style.display = 'block';
        localStorage.setItem('fanStatus', toggleButtonFan.checked);
    } else {
        statusTextFan.textContent = 'Off';
        iconfanon.style.display = 'none';
        iconfanoff.style.display = 'block';
        localStorage.setItem('fanStatus', null);
    }
}


function toggleButtonLightAction() {
    if (toggleButtonLight.checked) {
        sendControl(buildObjectControl());
        localStorage.setItem('lightStatus', toggleButtonLight.checked);
    } else {
        sendControl(buildObjectControl());
        localStorage.setItem('lightStatus', null);
    }
}

toggleButtonLight.addEventListener('click',toggleButtonLightAction);

function toggleButtonFanAction() {
    if (toggleButtonFan.checked) {
        sendControl(buildObjectControl());
        localStorage.setItem('fanStatus', toggleButtonFan.checked);
    } else {
        sendControl(buildObjectControl());
        localStorage.setItem('fanStatus', null);
    }
}

toggleButtonFan.addEventListener('click',toggleButtonFanAction);

window.addEventListener('load', function() {
    const lightStatus = localStorage.getItem('lightStatus');
    if (lightStatus !== null) {
        toggleButtonLight.checked = (lightStatus === 'true');
    }

    const fanStatus = localStorage.getItem('fanStatus');
    if (fanStatus !== null) {
        toggleButtonFan.checked = (fanStatus === 'true');
    }
    toggleButtonFanUpdate();
    toggleButtonLightUpdate();
    sendControl(buildObjectControl());
});


function buildObjectControl(){
    let fan = toggleButtonFan.checked ? 1 : 0;
    let light = toggleButtonLight.checked ? 1 : 0;
    let datac = light.toString()+fan.toString();
    let objectControl = {
        topic: 'control',
        message : datac
        //     {
        //     light : light,
        //     fan : fan
        // }
    };
    return objectControl;
}

function checkRespond(res) {
    if (res === 'ok') {
        toggleButtonLightUpdate();
        toggleButtonFanUpdate();
    }
}

const sendControl = async (data) => {
    try {
        const response = await fetch('http://localhost:8080/api/v1/control', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            let res = await response.text();
            console.log(res);
            checkRespond(res); // Gọi hàm checkRespond chỉ khi response.ok là true
        } else {
            console.error('Error sending data:', response.status);
        }

        console.log('Data sent successfully');
    } catch (error) {
        console.error('Error sending data:', error.message);
    }
}




let datatime = [0];

let datatemp = [0];
let datalux = [0];
let datahum = [0];
let dataid = [0];

let cc = new Chart("myChart", {
    type: "line",
    data: {
        labels: datatime,
        datasets: [{
            label: "Nhiệt độ",
            backgroundColor: "red",
            data: datatemp,
            borderColor: "red",
            fill: false,
            yAxisID: 'y'
        }, {
            label: "Độ ẩm",
            data: datahum,
            backgroundColor: "green",
            borderColor: "green",
            fill: false,
            yAxisID: 'y'
        }, {
            label: "Ánh sáng",
            data: datalux,
            backgroundColor: "blue",
            borderColor: "blue",
            fill: false,
            yAxisID: 'y1'
        }]
    },
    options: {
        legend:{display: true},
        responsive: true,
        scales: {
            y: {
                type: 'linear',
                display: true,
                position: 'left',
                scaleLabel: {
                    display: true,
                    labelString: 'Temperature (°C)'
                }
            },
            y1: {
                type: 'linear',
                display: true,
                position: 'right',
                scaleLabel: {
                    display: true,
                    labelString: 'Humidity (%)'
                },
                // grid line settings
                grid: {
                    drawOnChartArea: false, // only want the grid lines for one axis to show up
                },
            },
        },
    }
});


function loopdata(){
    getlatestsensor();

    if(datatemp.length > 10){
        datatemp.shift();
    }
    if(datahum.length > 10){
        datahum.shift();
    }
    if(datalux.length > 10){
        datalux.shift();
    }
    if(datatime.length > 10){
        datatime.shift();
    }

    let temp = Number(datatemp[datatemp.length - 1 ]);
    let hum = Number(datahum[datahum.length - 1 ]);
    let lux = Number(datalux[datalux.length - 1 ]);
    temp_val.textContent = temp + '°C';
    hum_val.textContent = hum + '%';
    lux_val.textContent = lux + ' Lux';

    setColor(1,temp);
    setColor(2,hum);
    setColor(3,lux);
    cc.update();
}

function setColor(id , value){
    if(id === 1){
        temp.style.background = `linear-gradient(180deg, #db5334 ${value /2 }%, #8B0000 ${(100 -value)/2 +50}% )`
    }
    if(id === 2){
        hum.style.background = `linear-gradient(180deg,#2874A6 ${value / 2}%,  #1B4F72 ${(100 -value)/2 +50}% )`
    }
    if(id === 3){
        lux.style.background =  `linear-gradient(180deg,#F4D03F  ${value / 2 - 50 }%, #B7950B ${(value)/2 +50}% )`
    }
}

const get10latestsensor = async() => {
    let response = await fetch('http://localhost:8080/api/v1/get10latestsensor');
    let data = await response.json();
    for(var i = data.length -1 ; i >=0 ; i-- ){
        var item = data[i];
        var id = Number(item.id);
        var temp = Number(item.temp);
        var hum = Number(item.hum);
        var lux = Number(item.lux);
        var time = new Date(item.date);
        var timeformat = String(time.getHours()+':'+time.getMinutes()+':'+time.getSeconds());

        dataid.push(id);
        datatemp.push(temp);
        datahum.push(hum);
        datalux.push(lux);
        datatime.push(timeformat);

    }
}

const getlatestsensor = async() => {
    let response = await fetch('http://localhost:8080/api/v1/getlatestsensor');
    let data = await response.json();
    let id = Number(data.id);
    if(!dataid.includes(id)){
        if(datatemp.length > 10){
            datatemp.shift();
        }
        if(datahum.length > 10){
            datahum.shift();
        }
        if(datalux.length > 10){
            datalux.shift();
        }
        if(datatime.length > 10){
            datatime.shift();
        }
        var temp = Number(data.temp);
        var hum = Number(data.hum);
        var lux = Number(data.lux);
        var time = new Date(data.date);
        var timeformat = String(time.getHours()+':'+time.getMinutes()+':'+time.getSeconds());

        datatemp.push(temp);
        datahum.push(hum);
        datalux.push(lux);
        datatime.push(timeformat);
    }

}

window.setInterval(loopdata , 5000);
window.addEventListener('load',get10latestsensor);