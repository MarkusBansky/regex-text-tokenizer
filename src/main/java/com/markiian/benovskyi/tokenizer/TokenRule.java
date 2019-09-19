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

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a pair of a <strong>regular expression</strong> and corresponding <strong>token</strong>.
 * Can be created, set ignored, and matched with word. Overrides equals methods.
 */
public class TokenRule {

    /**
     * The regular expression which is matched to input word.
     */
    private String _regularExpression;

    /**
     * The type or <strong>token</strong> name that this component represents.
     */
    private String _tokenName;

    /**
     * Flag used to mark this component as ignored. If it is set to true, then this token is ignored during tokenization process.
     * Default is <strong>false</strong>.
     */
    private Boolean _ignored;


    /**
     * Create new instance of TokenRule with regular expression and type as parameters.
     * Both parameters must be unique within single {@link Tokenizer tokenizer}.
     * @param regularExpression The RegEx string for matching.
     * @param type The <strong>token</strong> type to represent.
     */
    TokenRule(String regularExpression, String type) {
        this._regularExpression = regularExpression;
        this._tokenName = type;

        _ignored = false;
    }

    /**
     * Get token name for current component.
     * @return Name of the <strong>token</strong> of type {@link String}
     */
    String getTokenName() {
        return this._tokenName;
    }

    /**
     * Main function to check if the <strong>word</strong> of type {@link String} <strong>is matching</strong> the <strong>regular expression</strong> string of this token.
     * Returns <strong>true</strong> if does and <strong>false</strong> if doesn't.
     * @param word The word to match with regex.
     * @return Returns <strong>true</strong> if word <strong>matches</strong> the regular expression of this token.
     */
    Boolean isMatching(String word) {
        return Pattern.matches(this._regularExpression, word);
    }

    /**
     * Set this token ignoring status.
     * @param ignore Set <strong>true</strong> to <strong>ignore</strong> and <strong>false</strong> to <strong>include</strong>.
     */
    void setIgnored(@SuppressWarnings("SameParameterValue") Boolean ignore) {
        this._ignored = ignore;
    }

    /**
     * Get status of the token whether it is ignored.
     * @return <strong>True</strong> if is ignored and <strong>false</strong> otherwise.
     */
    Boolean isIgnored() {
        return this._ignored;
    }

    /**
     * Overrides default behaviour to compare both objects by RegEx and Ignored Status.
     * @param o Object to compare to.
     * @return True if both objects have the same properties or are equal instances.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenRule)) return false;
        TokenRule tokenRule = (TokenRule) o;
        return _regularExpression.equals(tokenRule._regularExpression) &&
                _ignored.equals(tokenRule._ignored);
    }

    /**
     * Overrides default behaviour.
     * @return Hash of regular expression and ignored status.
     */
    @Override
    public int hashCode() {
        return Objects.hash(_regularExpression, _ignored);
    }
}