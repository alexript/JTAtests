/*
 * Copyright 2023 alexript.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.napilnik.entitymodel;

import java.util.Date;

/**
 *
 * @author alexript
 */
public class Headers {

    //<editor-fold defaultstate="collapsed" desc="Nice test results delimiters.">
    public static Date printTestHeader(String name) {
        Date now = new Date();
        System.out.println("-ts- Test: %1$s -------------- <start: %2$tH:%2$tM:%2$tS> ---".formatted(name, now));
        return now;
    }

    public static void printTestFooter(String name) {
        printTestFooter(name, null);
    }

    public static void printTestFooter(String name, Date startNow) {
        if (startNow == null) {
            System.out.println("-te- Test: %s -------------- <end: %2$tH:%2$tM:%2$tS> ---\n".formatted(name, new Date()));
        } else {
            Date now = new Date();
            System.out.println("-te- Test: %1$s -------------- <end: %2$tH:%2$tM:%2$tS, len: %3$dms> ---\n".formatted(name, now, now.getTime() - startNow.getTime()));
        }
    }
//</editor-fold>

}
