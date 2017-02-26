package com.jeffjohnson.boojapp;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class Realtor {
    private String firstName;
    private String lastName;
    private String[] phoneNumber;
    private String office;
    private String photoUrl;

    public Realtor(String firstName, String lastName, String[] phoneNumber, String office, String photoUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.office = office;
        this.photoUrl = photoUrl;
    }

    public String getFirstName() {
        return firstName;
    }



    public String getLastName() {
        return lastName;
    }

    public String[] getPhoneNumber() {
        return phoneNumber;
    }

    public String getOffice() {
        return office;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public static class RealtorDeserializer implements JsonDeserializer<Realtor>{
        @Override
        public Realtor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            return new Realtor(
                    deserialize(object,"first_name"),
                    deserialize(object,"last_name"),
                    //split the phone number string between a - or a .
                    // and limit the split array to 3 Strings
                    object.get("phone_number").getAsString().split("-|[.]", 3),
                    deserialize(object, "office"),
                    //replace all backslashes with an empty string. need to escape backslash
                    deserialize(object, "photo").replace("\\", "")
            );
        }

        private String deserialize(JsonObject object, String key){
            JsonElement element = object.get(key);
            return !element.isJsonNull() ?  element.getAsString() : "";
        }
    }
}
