/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.crafter.engine.service.filter;

import org.craftercms.crafter.core.service.Item;
import org.craftercms.crafter.core.service.ItemFilter;

import java.util.Arrays;

/**
 * {@link ItemFilter} that rejects an item if its name matches any one of a list of regexes.
 *
 * @author Alfonso Vásquez
 */
public class ExcludeByNameItemFilter implements ItemFilter {

    private String[] excludeRegexes;

    public ExcludeByNameItemFilter(String excludeRegex) {
        excludeRegexes = new String[1];
        excludeRegexes[0] = excludeRegex;
    }

    public ExcludeByNameItemFilter(String[] excludeRegexes) {
        this.excludeRegexes = excludeRegexes;
    }

    @Override
    public boolean runBeforeProcessing() {
        return true;
    }

    @Override
    public boolean runAfterProcessing() {
        return false;
    }

    @Override
    public boolean accepts(Item item, boolean runningBeforeProcessing) {
        for (String regex : excludeRegexes) {
            if (item.getName().matches(regex)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "ExcludeByNameItemFilter[" +
                "excludeRegexes=" + (excludeRegexes == null ? null : Arrays.asList(excludeRegexes)) +
                ']';
    }

}