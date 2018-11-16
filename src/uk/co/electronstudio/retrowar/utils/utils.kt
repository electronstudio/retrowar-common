// <editor-fold desc="Copyright 2018 Richard Smith">
/*
    Copyright 2018 Richard Smith.

    RetroWar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RetroWar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RetroWar.  If not, see <http://www.gnu.org/licenses/>.
*/
// </editor-fold>
package uk.co.electronstudio.retrowar.utils

/**
 * Global scope stuff that IntelliJ doesn't like being in the actual files where it is used
 */

/** @suppress */
fun Float.sqrt(): Float {
    val f = this
    var y =
        java.lang.Float.intBitsToFloat(0x5f375a86 - (java.lang.Float.floatToIntBits(f) shr 1)) // evil floating point bit level hacking -- Use 0x5f375a86 instead of 0x5f3759df, due to slight accuracy increase. (Credit to Chris Lomont)
    y *= (1.5f - 0.5f * f * y * y) // Newton step, repeating increases accuracy
    return f * y
}

/** @suppress */
fun Float.sqrta(): Float {
    val f = this
    return f * java.lang.Float.intBitsToFloat(0x5f375a86 - (java.lang.Float.floatToIntBits(f) shr 1)) // evil floating point bit level hacking -- Use 0x5f375a86 instead of 0x5f3759df, due to slight accuracy increase. (Credit to Chris Lomont)
}
