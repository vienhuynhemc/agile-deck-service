package com.axonactive.agiletools.agiledeck;

import java.security.SecureRandom;

public class Faker {

    String[] fruits = { "Abiu", "Ackee", "Apple", "Apricot", "Avocado", "Banana", "Bilberry", "Blackberry",
            "Blackcurrant", "Black sapote", "Blueberry", "Boysenberry", "Breadfruit", "Cactus pear", "Cempedak",
            "Crab apple", "Currant", "Cherry", "Chico fruit", "Cloudberry", "Coconut", "Cranberry", "Damson", "Date",
            "Durian", "Egg Fruit", "Elderberry", "Feijoa", "Fig", "Goji berry", "Gooseberry", "Grape", "Raisin",
            "Grapefruit", "Guava", "Honeyberry", "Huckleberry", "Jabuticaba", "Jackfruit", "Jambul", "Jostaberry",
            "Jujube", "Juniper berry", "Kiwifruit", "Kumquat", "Lemon", "Lime", "Loganberry", "Loquat", "Longan",
            "Lulo", "Lychee", "Mamey Apple", "Mamey Sapote", "Mango", "Mangosteen", "Marionberry", "Melon",
            "Cantaloupe", "Galia melon", "Honeydew", "Watermelon", "Miracle fruit", "Monstera deliciosa", "Mulberry",
            "Nance", "Nectarine", "Orange", "Clementine", "Mandarine", "Tangerine", "Papaya", "Passionfruit", "Peach",
            "Pear", "Persimmon", "Plantain", "Plum", "Pineapple", "Pineberry", "Pomegranate", "Pomelo", "Quince",
            "Raspberry", "Salmonberry", "Redcurrant", "Rose apple", "Salal berry", "Salak", "Satsuma", "Soursop",
            "Star apple", "Star fruit", "Strawberry", "Surinam cherry", "Tamarillo", "Tamarind", "Tangelo", "Tayberry",
            "Tomato", "Ugli fruit", "White currant", "White sapote", "Yuzu" };

    public String fruit() {
        SecureRandom random = new SecureRandom();
        int i = random.nextInt(fruits.length);
        return fruits[i];
    }

    public Faker food() {
        return this;
    }
}
