package com.henry.portfolio.position;

import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

public class PositionService {

    private ConcurrentHashMap<String, Position> positions;
    private String positionFile;
    private ResourceLoader resourceLoader;

    public PositionService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        positions = positions();
    }

    public Position position(String ticker){
        if(positions.get(ticker) == null){
                positions = positions();
        }
        return positions.get(ticker);
    }

    /**
     * Assuming only one portfolio, so no need to specify portfolio ticker here
     */
    private ConcurrentHashMap<String, Position> positions()  {
        String positionFileName = "classpath:portfolioPositions.csv";

        ConcurrentHashMap<String, Position> positions = new ConcurrentHashMap<>();
        String line = "";
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resourceLoader.getResource(positionFileName).getInputStream()))){
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] position = line.split(",");
                positions.put(position[0],
                        new Position(position[0], position[1], BigDecimal.valueOf(Double.valueOf(position[2]))));
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return positions;
    }

}
