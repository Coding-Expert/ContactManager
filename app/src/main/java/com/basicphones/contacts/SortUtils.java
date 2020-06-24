package com.basicphones.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortUtils {

    public static List<ContactInfo> getContacts(List<ContactInfo> contacts){
        List<ContactInfo> contactsList = new ArrayList<>();
        List<String> contact_char_sortname = new ArrayList<>();
        List<String> contactId = new ArrayList<>();
        List<String> contact_num_sortname = new ArrayList<>();
        List<ContactInfo> contact_char = new ArrayList<>();

        if(contacts.size() > 0){
            for(int i = 0; i < contacts.size(); i++){
                if(checkCharacters(contacts.get(i).getName().charAt(0))){
                    contact_char_sortname.add(contacts.get(i).getName());
//                    contactId.add(contacts.get(i).getmContact_id());
                }
                if(checkNumerical(contacts.get(i).getName().charAt(0))){
                    contact_num_sortname.add(contacts.get(i).getName());
                }
            }
            Collections.sort(contact_char_sortname, String.CASE_INSENSITIVE_ORDER);
            Collections.sort(contact_num_sortname);
            if(contact_char_sortname.size() > 0){
                for(int i = 0; i < contact_char_sortname.size(); i++){
                    for(int j = 0; j < contacts.size(); j++){
                        if(contacts.get(j).getName().equals(contact_char_sortname.get(i))){
                            if(!contact_char.contains(contacts.get(j))){
                                contact_char.add(contacts.get(j));
                                break;
                            }

                        }
                    }
                }
            }
            if(contact_num_sortname.size() > 0){
                for(int i = 0; i < contact_num_sortname.size(); i++){
                    for(int j = 0; j < contacts.size(); j++){
                        if(contacts.get(j).getName().equals(contact_num_sortname.get(i))){
                            contactsList.add(contacts.get(j));
                            break;
                        }
                    }
                }
            }
            for(int i = 0; i < contact_char.size(); i++){
                contactsList.add(contact_char.get(i));
            }
        }
        return contactsList;
    }

    public static boolean checkCharacters(char ch){
        boolean check_flag = false;
        String character = "" + ch;
        if (character.matches(".*[A-Z].*") || character.matches(".*[a-z].*")){
            check_flag = true;
            return check_flag;
        }
        return check_flag;
    }
    public static boolean checkNumerical(char ch) {
        boolean check_flag = false;
        String regexStr = "^[0-9]*$";
        String character = "" + ch;
        if(character.trim().matches(regexStr) || character.equals("+"))
        {
            check_flag = true;
            return check_flag;
        }
        return check_flag;
    }

    public static List<String> getSortString(List<ContactInfo> contacts){
        List<String> sectionList = new ArrayList<>();
        List<String> char_list = new ArrayList<>();
        if(contacts.size() > 0){
            for(int i = 0; i < contacts.size(); i++){
                String first_char = "" + contacts.get(i).getName().charAt(0);
                if(first_char.toUpperCase().matches("^[0-9]*$") || first_char.toUpperCase().equals("+")){
                    if(!sectionList.contains("#")) {
                        sectionList.add("#");
                    }
                }
                if(first_char.toUpperCase().matches(".*[A-Z].*")){
                    if(!char_list.contains(first_char.toLowerCase())){
                        char_list.add(first_char.toLowerCase());
                    }
                }
            }
            Collections.sort(char_list);
            for(int i = 0; i < char_list.size(); i++){
                sectionList.add(char_list.get(i));
            }
        }
        return sectionList;
    }
}
