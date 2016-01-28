package com.delacrmi.persistences;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Legal on 12/11/2015.
 */
public class EntityFilter {

    private String[] whereCondition;
    private String[] whereValues;

    public EntityFilter(String[] whereConditions, String[] whereValues){
        this.whereCondition = whereConditions;
        this.whereValues = whereValues;
    }

    public String getConditions(EntityFilter.ParamType paramType){
        String conditions = "";

        if ( whereCondition != null && whereCondition.length > 0 ) {
            for (int i = 0; i < whereCondition.length; i++) {
                switch (paramType) {
                    case TWO_POINT:
                        conditions += whereCondition[i] + " = :" + whereCondition[i]+" ";
                        break;
                    case QUESTION_MARK:
                        conditions += whereCondition[i] + " = ? ";
                        break;
                }
                if ( whereCondition.length > 1 && whereCondition.length != (i+1) )
                    conditions+=" and ";
            }
        }

        return conditions;
    }

    public JSONArray getValues() {
        JSONArray array = new JSONArray();

        if ( whereValues != null && whereValues.length > 0 ) {
            for (int i = 0; i < whereValues.length; i++) {
                try {
                    array.put(i, whereValues[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return array;
    }

    public enum ParamType {
        QUESTION_MARK("?"), TWO_POINT(":");

        String arg;

        ParamType(String arg) {
            this.arg = arg;
        }
    }

}
