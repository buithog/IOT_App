package com.example.iotapp;

import com.example.iotapp.entity.Control;
import com.example.iotapp.entity.Sensor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/api/v1")
public class Controller {
    @Autowired
    MqttGateway mqttGateway;
    @Autowired
    AppService appService;
    @Autowired
    MqttBean mqttBean;

    @PostMapping("/control")
    public ResponseEntity<?> publish(@RequestBody String mqttMessage){
        try {
            JsonObject convertedJson = new Gson().fromJson(mqttMessage, JsonObject.class);
            mqttGateway.sendToMqtt(convertedJson.get("message").toString(), convertedJson.get("topic").toString().substring(1,8));
            System.out.println(convertedJson.toString());
            System.out.println(convertedJson.get("message").toString());
            CompletableFuture<Object> respondControlMessage = mqttBean.getRespondControlMessage();
            Object result = respondControlMessage.get(); // Đợi tin nhắn từ topic "respondcontrol"
            appService.saveDataControl(convertedJson.get("message").toString());
            mqttBean.setRespondControlMessage(new CompletableFuture<>());
            return ResponseEntity.ok().body(result);

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok("fail");
        }
    }

    @CrossOrigin
    @GetMapping("/sensor")
    public ResponseEntity<Sensor> getall(){
        return ResponseEntity.ok(appService.getListSensor().get(1));
    }

    @CrossOrigin
    @GetMapping("/get10latestsensor")
    public ResponseEntity<List<Sensor>> get10LatestSensor(){
        return ResponseEntity.ok(appService.getTenLatestSensor());
    }
    @CrossOrigin
    @GetMapping("/getlatestsensor")
    public ResponseEntity<Sensor> getLatestSensor(){
        return ResponseEntity.ok(appService.getLatestSensor());
    }
    @CrossOrigin
    @GetMapping("/getcontrol")
    public ResponseEntity<List<Control>> getAllControl(){
        return ResponseEntity.ok(appService.getListControl());
    }
    @CrossOrigin
    @GetMapping("/getCount")
    public ResponseEntity<Long> getAllCountSensor(){
        return ResponseEntity.ok(appService.getTotalSensorCount());
    }

    @CrossOrigin
    @GetMapping("/getCountControls")
    public ResponseEntity<Long> getAllCountControls(){
        return ResponseEntity.ok(appService.getTotalControlsCount());
    }

    @CrossOrigin
    @GetMapping("/getdatasensor")
    public ResponseEntity<List<Sensor>> getByPageSensor(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam(defaultValue = "0") int pagenum
    ) throws Exception {
       try {
           Sort.Direction  direction = Sort.Direction.valueOf(sortDirection);
           Pageable page = PageRequest.of(pagenum,pagesize,Sort.by(direction,sortBy));
           return ResponseEntity.ok(appService.getByPageSensor(page));
       }catch (Exception e){
           e.printStackTrace();
           throw new Exception("Fail to get Sensor with page");
       }
    }
    @CrossOrigin
    @GetMapping("/getdatacontrol")
    public ResponseEntity<List<Control>> getByPageControl(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam(defaultValue = "0") int pagenum
    ) throws Exception {
        try {
            Sort.Direction  direction = Sort.Direction.valueOf(sortDirection);
            Pageable pageable = PageRequest.of(pagenum,pagesize,Sort.by(direction,sortBy));
            return ResponseEntity.ok(appService.getByPageControl(pageable));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Fail to get Control with page");
        }
    }

    @CrossOrigin
    @GetMapping("/searchSensor")
    public ResponseEntity<List<Sensor>> getSensorByKeyWord(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam(defaultValue = "0") int pagenum,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String col
    ) throws Exception {
        try {
            System.out.println(sortBy + " " + sortDirection + " " + pagesize + " " + pagenum + " " + keyword + " " + col);
            return ResponseEntity.ok(appService.searchSensorbyKeyword(col,keyword,sortBy,pagesize,pagenum,sortDirection));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Fail to get Control with page");
        }
    }
    @CrossOrigin
    @GetMapping("/searchControl")
    public ResponseEntity<List<Control>> getByControlKeyWord(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam(defaultValue = "0") int pagenum,
            @RequestParam(defaultValue = "") String keyword
    ) throws Exception {
        try {
            System.out.println(sortBy + " " + sortDirection + " " + pagesize + " " + pagenum + " " + keyword + " " );
            return ResponseEntity.ok(appService.searchControlbyKeyword(keyword ,sortBy ,pagesize ,pagenum,sortDirection));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Fail to get Control with page");
        }
    }
}
