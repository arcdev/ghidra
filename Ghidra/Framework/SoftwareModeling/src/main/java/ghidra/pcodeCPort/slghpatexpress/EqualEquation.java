/* ###
 * IP: GHIDRA
 * REVIEWED: YES
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
package ghidra.pcodeCPort.slghpatexpress;

import generic.stl.VectorSTL;
import ghidra.pcodeCPort.context.*;
import ghidra.sleigh.grammar.Location;

public class EqualEquation extends ValExpressEquation {

    public EqualEquation(Location location, PatternValue l, PatternExpression r) {
        super(location, l, r);
    }

    @Override
    public void genPattern(VectorSTL<TokenPattern> ops) {
        long lhsmin = lhs.minValue();
        long lhsmax = lhs.maxValue();
        VectorSTL<PatternValue> semval = new VectorSTL<PatternValue>();
        VectorSTL<Long> min = new VectorSTL<Long>();
        VectorSTL<Long> max = new VectorSTL<Long>();
        VectorSTL<Long> cur = new VectorSTL<Long>();
        int count = 0;

        rhs.listValues(semval);
        rhs.getMinMax(min, max);
        cur = min.copy();

        do {
            long val = rhs.getSubValue(cur);
            if ((val >= lhsmin) && (val <= lhsmax)) {
                if (count == 0)
                    setTokenPattern(ExpressUtils.buildPattern(lhs, val, semval, cur));
                else
                    setTokenPattern(getTokenPattern().doOr(ExpressUtils.buildPattern(lhs, val, semval, cur)));
                count += 1;
            }
        } while (ExpressUtils.advance_combo(cur, min, max));
        if (count == 0) {
            throw new SleighError("Equal constraint is impossible to match", location);
        }
    }

}
