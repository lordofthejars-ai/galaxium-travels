package com.galaxium.booking.dto;

import java.util.Map;

/**
 * Add-on data transfer object.
 * Matches Python backend's AddOn schema.
 */
public class AddOnDto {
    public String id;
    public String name;
    public Integer price;
    public Boolean selected = false;
    public String description;
    public String icon;

    public AddOnDto() {
    }

    public AddOnDto(String id, String name, Integer price, String description, String icon) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.icon = icon;
    }

    public static AddOnDto from(Map<String, ?> data) {
        AddOnDto addOnDto = new AddOnDto();
        addOnDto.id = (String) data.get("id");
        addOnDto.name = (String) data.get("name");
        addOnDto.icon = (String) data.get("icon");
        addOnDto.price = (Integer) data.get("price");
        addOnDto.selected = (Boolean) data.get("selected");
        addOnDto.description = (String) data.get("description");

        return addOnDto;
    }
}

// Made with Bob
