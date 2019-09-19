/*
 * Copyright (c) 2019. Markiian Benovskyi .
 *
 * IN NO EVENT SHALL MARKIIAN BENOVSKYI BE LIABLE TO ANY PARTY FOR DIRECT,
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST
 * PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF MARKIIAN BENOVSKYI HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * MARKIIAN BENOVSKYI SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF
 * ANY, PROVIDED HEREUNDER IS PROVIDED "AS IS". MARKIIAN BENOVSKYI HAS NO
 * OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
 * MODIFICATIONS.
 */

package com.markiian.benovskyi.tokenizer;

import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerTest {

    @Test
    public void tokenize() {
        String dataSentence = "Українка Бережна стала віце-чемпіонкою світу з боротьби";

        Tokenizer t = new Tokenizer();

        t.addTokenRule("^,$", "comma");
        t.addTokenRule("^\\.$", "full stop");
        t.addTokenRule("^\\\"$", "quote");
        t.addTokenRule("^[-‐‑\u00AD]$", "hyphen");
        t.addTokenRule("^\\s?[‒–—]\\s?$", "dash");
        t.addTokenRule("^[\\s    ]+$", "whitespace");
        t.addTokenRule("^[а-яА-ЯіїґєІЇҐЄ]+$", "word");
        t.addTokenRule("^([а-яА-ЯіїґєІЇҐЄ0-9]+[-]+[а-яА-ЯіїґєІЇҐЄ0-9]+)+$", "complex word");
        t.addTokenRule("^\\d+(\\.\\d+)?$", "number");
        t.addTokenRule("^([0-9]*[+][0-9]*)+$", "special name");

        t.setToIgnore("whitespace");

        try {
            t.tokenize(dataSentence, (word, token) -> {
                System.out.println(String.format(" ( word: %s; token: %s ) ", word, token));

                assertNotNull(word);
                assertNotNull(token);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}