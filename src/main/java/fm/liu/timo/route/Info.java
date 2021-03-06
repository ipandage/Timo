/*
 * Copyright 2015 Liu Huanting.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package fm.liu.timo.route;

/**
 * @author Liu Huanting 2015年6月4日
 */
public interface Info {
    public int NEED_MERGE   = 1;
    public int HAS_GROUPBY  = 2;
    public int HAS_ORDERBY  = 4;
    public int HAS_DISTINCT = 8;
    public int HAS_LIMIT    = 16;
    public int TO_ALL_NODE  = 32;
}
