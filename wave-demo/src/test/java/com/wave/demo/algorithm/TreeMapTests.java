package com.wave.demo.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.wave.demo.dto.TestBean;
import com.wave.demo.dto.realestates;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

/**
 * @author lijinyang
 * @date 2021/7/14 17:00
 * tree map 测试
 */
@Slf4j
public class TreeMapTests {

    public List<String> calculateEarliestArriveTimeRangeList(List<String> reservationTimeRangeList, Date arriveTime) throws ParseException {
        TreeMap<Long, String> timeMap = new TreeMap<>();
        reservationTimeRangeList.forEach(e -> {
            String[] timePair = StringUtils.split(e, "-");
            if (timePair != null && timePair.length == 2) {
                Date endTime = null;
                try {
                    endTime = DateUtils.parseDate(timePair[1], "HH:mm");
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                timeMap.put(endTime.getTime(), e);
            }
        });
        log.info("==== {}", arriveTime.getTime());
        Date date = DateUtils.parseDate(DateFormatUtils.format(arriveTime, "HH:mm"), "HH:mm");
        log.info("=== format {}", date.getTime());
        SortedMap<Long, String> tailMap = timeMap.tailMap(date.getTime(), false);
        if (CollectionUtils.isEmpty(tailMap)) {
            return reservationTimeRangeList;
        }
        return new ArrayList<>(tailMap.values());
    }

    @Test
    public void testTailTime() throws ParseException {
        List<String> reservationTimeRangeList = Arrays.asList("09:00-13:00", "14:00-17:00", "18:00-22:00");
        Date date = DateUtils.parseDate("2021-07-14 14:30:00", "yyyy-MM-dd HH:mm:ss");
        List<String> res = calculateEarliestArriveTimeRangeList(reservationTimeRangeList, date);
        log.info("==== res {}", res);
    }

    @Test
    public void testTimeFormat() throws ParseException {
        String time = "9:00";
        Date res = DateUtils.parseDate(time, "HH:mm");
        log.info("=== {}", res);
    }

    @Test
    public void testEmptyList() {
        List list = Collections.EMPTY_LIST;

        List resList = new ArrayList();
        resList.addAll(list);
        log.info("end");
    }

    @Test
    public void testArrays() {
        Set<TestBean> beanSet = new HashSet<>();
        beanSet.add(new TestBean("1", 1));
        beanSet.add(new TestBean("2", 2));
        beanSet.add(new TestBean("3", 3));

        log.info("=== set");
        for (TestBean testBean : beanSet) {
            log.info("=== {}", testBean.hashCode());
        }

        log.info("===after copy");
        ArrayList<TestBean> beanArrayList = new ArrayList<>(beanSet);
        for (TestBean testBean : beanArrayList) {
            log.info("=== {}", testBean.hashCode());
        }
    }

    @Test
    public void testXmlToJava() throws JsonProcessingException, JAXBException {
        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<realestates>\n" +
                "<externalId>100011</externalId>\n" +
                "<title>RestAPI - Immobilienscout24 Testobjekt! +++BITTE+++ NICHT kontaktieren - Industry</title>\n" +
                "<creationDate>2013-12-06T15:12:09</creationDate>\n" +
                "<lastModificationDate>2014-07-08T15:12:09</lastModificationDate>\n" +
                "<thermalCharacteristic>32.678</thermalCharacteristic>\n" +
                "<energyConsumptionContainsWarmWater>YES</energyConsumptionContainsWarmWater>\n" +
                "<buildingEnergyRatingType>ENERGY_CONSUMPTION</buildingEnergyRatingType>\n" +
                "<additionalArea>435.017</additionalArea>\n" +
                "<numberOfFloors>three</numberOfFloors>\n" +
                "<additionalCosts>\n" +
                "<value>249.014</value>\n" +
                "<currency>EUR</currency>\n" +
                "<marketingType>RENT</marketingType>\n" +
                "<priceIntervalType>MONTH</priceIntervalType>\n" +
                "</additionalCosts>\n" +
                "</realestates>";
        /*XmlMapper xmlMapper = new XmlMapper();
        realestates value = xmlMapper.readValue(test, realestates.class);
        log.info("==={}", value);*/

        JAXBContext jaxbContext = JAXBContext.newInstance(realestates.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        realestates res = (realestates) jaxbUnmarshaller.unmarshal(new StringReader(test));
        log.info("==== {}", res);
    }

}
