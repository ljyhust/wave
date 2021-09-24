package com.wave.demo.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lijinyang
 * @date 2021/7/22 20:22
 */
@Data
@XmlRootElement(name="realestates")
public class realestates {
    private String externalId;
    private String title;
    private String creationDate;
    private String lastModificationDate;
    private String thermalCharacteristic;
    private String energyConsumptionContainsWarmWater;
    private String buildingEnergyRatingType;
    private String additionalArea;
    private String numberOfFloors;
    private List<additionalCosts> additionalCosts;
}
