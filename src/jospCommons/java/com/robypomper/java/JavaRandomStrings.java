/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.java;

import java.util.Random;

public class JavaRandomStrings {

    public static String randomAlfaString(int length) {
        int leftLimit = 'A'; // letter 'a'
        int rightLimit = 'Z'; // letter 'z'
        int targetStringLength = length;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static String randomListString(String[] list) {
        int index = new Random().nextInt(list.length);
        return list[index];
    }

    public static String randomFruitsString() {
        return randomListString(FRUITS);
    }

    public static String randomAnimalsString() {
        return randomListString(ANIMALS);
    }

    public static final String[] ANIMALS = new String[]{
            "Crow",
            "Peacock",
            "Dove",
            "Sparrow",
            "Goose",
            "Stork",
            "Pigeon",
            "Turkey",
            "Hawk",
            "Bald_eagle",
            "Raven",
            "Parrot",
            "Flamingo",
            "Seagull",
            "Ostrich",
            "Swallow",
            "Black_bird",
            "Penguin",
            "Robin",
            "Swan",
            "Owl",
            "Woodpecker",
            "Squirrel",
            "Dog",
            "Chimpanzee",
            "Ox",
            "Lion",
            "Panda",
            "Walrus",
            "Otter",
            "Mouse",
            "Kangaroo",
            "Goat",
            "Horse",
            "Monkey",
            "Cow",
            "Koala",
            "Mole",
            "Elephant",
            "Leopard",
            "Hippopotamus",
            "Giraffe",
            "Fox",
            "Coyote",
            "Hedgehong",
            "Sheep",
            "Deer",
            "Giraffe",
            "Woodpecker",
            "Camel",
            "Starfish",
            "Koala",
            "Alligator",
            "Owl",
            "Tiger",
            "Bear",
            "Blue_whale",
            "Coyote",
            "Chimpanzee",
            "Raccoon",
            "Lion",
            "Arctic_wolf",
            "Crocodile",
            "Dolphin",
            "Elephant",
            "Squirrel",
            "Snake",
            "Kangaroo",
            "Hippopotamus",
            "Elk",
            "Fox",
            "Gorilla",
            "Bat",
            "Hare",
            "Toad",
            "Frog",
            "Deer",
            "Rat",
            "Badger",
            "Lizard",
            "Mole",
            "Hedgehog",
            "Otter",
            "Reindeer",
            "Crab",
            "Fish",
            "Seal",
            "Octopus",
            "Shark",
            "Seahorse",
            "Walrus",
            "Starfish",
            "Whale",
            "Penguin",
            "Jellyfish",
            "Squid",
            "Lobster",
            "Pelican",
            "Clams",
            "Seagull",
            "Dolphin",
            "Shells",
            "Sea_urchin",
            "Cormorant",
            "Otter",
            "Pelican",
            "Sea_anemone",
            "Sea_turtle",
            "Sea_lion",
            "Coral",
            "Moth",
            "Bee",
            "Butterfly",
            "Spider",
            "Ladybird",
            "Ant",
            "Dragonfly",
            "Fly",
            "Mosquito",
            "Grasshopper",
            "Beetle",
            "Cockroach",
            "Centipede",
            "Worm",
            "Louse"
    };

    public static final String[] FRUITS = new String[]{
            "Açaí",
            "Akee",
            "Apple",
            "Apricot",
            "Avocado",
            "Banana",
            "Bilberry",
            "Blackberry",
            "Blackcurrant",
            "Black_sapote",
            "Blueberry",
            "Boysenberry",
            "Cactus_pear",
            "Crab_apple",
            "Currant",
            "Cherry",
            "Cherimoya",
            "Chico_fruit",
            "Cloudberry",
            "Coconut",
            "Cranberry",
            "Damson",
            "Date",
            "Dragonfruit",
            "Durian",
            "Elderberry",
            "Feijoa",
            "Fig",
            "Goji_berry",
            "Gooseberry",
            "Grape",
            "Raisin",
            "Grapefruit",
            "Guava",
            "Honeyberry",
            "Huckleberry",
            "Jabuticaba",
            "Jackfruit",
            "Jambul",
            "Japanese_plum",
            "Jostaberry",
            "Jujube",
            "Juniper_berry",
            "Kiwano",
            "Kiwifruit",
            "Kumquat",
            "Lemon",
            "Lime",
            "Loganberry",
            "Loquat",
            "Longan",
            "Lychee",
            "Mango",
            "Mangosteen",
            "Marionberry",
            "Melon",
            "Cantaloupe",
            "Galia_melon",
            "Honeydew",
            "Watermelon",
            "Miracle_fruit",
            "Mulberry",
            "Nectarine",
            "Nance",
            "Orange",
            "Blood_orange",
            "Clementine",
            "Mandarine",
            "Tangerine",
            "Papaya",
            "Passionfruit",
            "Peach",
            "Pear",
            "Persimmon",
            "Plantain",
            "Plum",
            "Prune",
            "Pineapple",
            "Pineberry",
            "Plumcot",
            "Pomegranate",
            "Pomelo",
            "Purple_mangosteen",
            "Quince",
            "Raspberry",
            "Salmonberry",
            "Rambutan",
            "Redcurrant",
            "Salal_berry",
            "Salak",
            "Satsuma",
            "Soursop",
            "Star_apple",
            "Star_fruit",
            "Strawberry",
            "Surinam_cherry",
            "Tamarillo",
            "Tamarind",
            "Tangelo",
            "Tayberry",
            "Ugli_fruit",
            "White_currant",
            "White_sapote",
            "Yuzu"
    };

}
