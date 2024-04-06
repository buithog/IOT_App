package com.example.iotapp;

import com.example.iotapp.dao.ControlDao;
import com.example.iotapp.dao.SensorDao;
import com.example.iotapp.entity.Control;
import com.example.iotapp.entity.Sensor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppService {
    @Autowired
    private SensorDao sensorDao;
    @Autowired
    private ControlDao controlDao;

    public void saveDataSensor(String data){

        JsonObject sensorJson = new Gson().fromJson(data,JsonObject.class);
        int temp = sensorJson.get("temperture").getAsInt();
        int hum = sensorJson.get("humidity").getAsInt();
        int lux = sensorJson.get("lux").getAsInt();
        Sensor sensor = new Sensor(temp,hum,lux);
        sensorDao.save(sensor);
    }
    public void saveDataControl(String data){
        int fan = Integer.valueOf(data.charAt(0));
        int light = Integer.valueOf(data.charAt(1));
        Control control = new Control(fan,light);
        controlDao.save(control);
    }

    public List<Control> getListControl(){
        return controlDao.findAll();
    }
    public List<Sensor> getListSensor(){
        return sensorDao.findAll();
    }
    public List<Sensor> getTenLatestSensor(){return sensorDao.getTenLatestSensor();}
    public Sensor getLatestSensor(){return sensorDao.getLatestSensor();}
    public List<Sensor> getByPageSensor(Pageable pageable){
        Page<Sensor> page =  sensorDao.findAll(pageable);
        List<Sensor> sensorList = page.getContent();
        return sensorList;
    }
    public List<Control> getByPageControl(Pageable pageable){
        Page<Control> page =  controlDao.findAll(pageable);
        List<Control> controls = page.getContent();
        return controls;
    }
    public List<Sensor> searchSensorbyKeyword(String col, String keyword , String sortby , int pagesize , int pagenum, String sortDirection ) throws Exception {
        int offset = pagesize*pagenum;
        Pageable pageable = PageRequest.of(pagenum,pagesize, Sort.Direction.valueOf(sortDirection),sortby);
        if(col.equals("id")){
            return sensorDao.searchSensorById(keyword,pageable).getContent();
        }else if(col.equals("date")){
            return sensorDao.searchSensorByDate(keyword,pageable).getContent();
        }else if(col.equals("temp")){
            return sensorDao.searchSensorByTemp(keyword,pageable).getContent();
        }else if (col.equals("lux")){
            return sensorDao.searchSensorByLux(keyword,pageable).getContent();
        }else if(col.equals("hum")){
            return sensorDao.searchSensorByHum(keyword,pageable).getContent();
        }else {
            throw new Exception("not found col for search");
        }
    }
    public List<Control> searchControlbyKeyword(String keyword , String sortby , int pagesize , int pagenum, String sortDirection ) throws Exception {
        Pageable pageable1 = PageRequest.of(pagenum, pagesize, Sort.Direction.valueOf(sortDirection), sortby);
        return controlDao.searchControlByDate(keyword, pageable1).getContent();
    }

    public Long getTotalSensorCount() {
        return sensorDao.countAllSensors();
    }

    public Long getTotalControlsCount() {
        return controlDao.countAllControls();
    }
}
