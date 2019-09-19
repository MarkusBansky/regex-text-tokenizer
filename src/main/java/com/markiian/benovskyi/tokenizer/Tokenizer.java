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

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Class used to extract custom named tokens from text.
 * You must set custom token rules with a regex string to match and token name to return on match.
 * Each token can be marked as ignored by the stream on the flow is required.
 * Every time a token is extracted a callback is executed to return the extracted values.
 */
public class Tokenizer extends ChunkStream {

    /**
     * Buffer data used to store any values that come after a token word that was found in chunk data.
     */
    private String _bufferedData;

    /**
     * Tokens used to recognize parts of text in the input data stream.
     */
    private List<TokenRule> _rules;

    /**
     * A function that is called when chunk has been processed.
     * First parameter is the word that is matched, the second parameter is the name of the tag that was matched.
     */
    private BiConsumer<String, String> _processingCallback;

    /**
     * Private unknown token, used when there is no token for chunk sequence, to omit exceptions.
     */
    private final TokenRule _unknown_ = new TokenRule("", "_unknown_");

    /**
     * Create instance of the class with default values.
     */
    public Tokenizer() {
        _bufferedData = "";
        _rules = new ArrayList<>();
        _processingCallback = null;
    }

    /**
     * Add a new regex token rule.
     * @param regex A regular expresion string, must be unique in the tokenizer instance.
     * @param tokenName The token name to return, must be unique in the tokenizer instance.
     * @throws KeyAlreadyExistsException If there already is such rule with the same regex and status.
     */
    public void addTokenRule(String regex, String tokenName) throws KeyAlreadyExistsException {
        TokenRule newRule = new TokenRule(regex, tokenName);

        if (_rules.stream().noneMatch(eRule -> eRule.equals(newRule))) {
            _rules.add(newRule);
        } else {
            throw new KeyAlreadyExistsException(String.format("Rule for %s with value (%s) already exists!", tokenName, regex));
        }
    }

    /**
     * Set specific token to be ignored during processing. Ignored tokens are not returned by the callback.
     * @param tokenName The name of existing token that is going to be ignored.
     */
    public void setToIgnore(String tokenName) {
        Optional<TokenRule> rule = _rules.stream().filter(eRule -> eRule.getTokenName().equalsIgnoreCase(tokenName)).findFirst();

        if (rule.isPresent()) {
            rule.get().setIgnored(true);
        } else {
            System.out.println(String.format("Could not find token %s in the rules list!", tokenName));
        }
    }

    /**
     * Extract tokens from specific text String.
     * @param text The text data to extract tokens from.
     * @param onEachToken Function callback to produce on each token extraction.
     */
    public void tokenize(String text, BiConsumer<String, String> onEachToken) {
        _processingCallback = onEachToken;

        openStream(text);
    }

    /**
     * A callback function from {@link ChunkStream} base.
     * @param chunk The message to process in a callback.
     */
    @Override
    protected void processChunk(CharSequence chunk) {
        Integer index = 0;
        while(index < chunk.length()) {
            int offset = Math.min(index + _processingStep, chunk.length());

            buildUp(chunk.subSequence(index, offset).toString());

            index += _processingStep;
        }

        this.flush();
    }

    /**
     * Processes the chunk and searches for most matching token there, when the token is found then it is returned
     * by the callback and the rest ob chunk is extracted and added to string buffer for later analysis.
     * Function is called recursively while the data processed is not equal to empty string.
     * @param data Chunk to process.
     */
    private void buildUp(String data) {
        // Gather data from buffer as well as from parameter
        String wholeData = _bufferedData.concat(data);
        _bufferedData = "";

        if (wholeData.length() == 0) {
            return;
        }

        // Get maximum index of the token
        int maxIndex = 0;
        for(; maxIndex < wholeData.length(); maxIndex++) {
            String buffer = wholeData.substring(0, maxIndex + 1);
            if (getMatchingRule(buffer) == null) {
                break;
            }
        }

        // No match found
        if (maxIndex == 0) {
            // Create an unknown token for single char
            returnToken(wholeData.substring(0, 1), _unknown_);

            // Then pass on single char
            this.buildUp(wholeData.substring(1));
        }
        // The whole string is matching
        else if (maxIndex == wholeData.length()) {
            this._bufferedData = wholeData;
        }
        // Some substring is matching
        else {
            String matchingString = wholeData.substring(0, maxIndex);
            TokenRule matchingRule = this.getMatchingRule(matchingString);

            assert matchingRule != null;
            returnToken(matchingString, matchingRule);

            this.buildUp(wholeData.substring(maxIndex));
        }
    }

    /**
     * Called at the end of streaming process to clear everything that was left in _bufferedData.
     */
    private void flush() {
        if (_bufferedData.length() == 0) {
            return;
        }

        TokenRule matchingRule = this.getMatchingRule(_bufferedData);

        if(matchingRule != null) {
            returnToken(_bufferedData, matchingRule);
        }
    }

    /**
     * Retrieve the regex rule that matches current string buffer.
     * @param buffer The string to match by rules for token.
     * @return {@link TokenRule Token} which regex matches the string buffer word, or <strong>null</strong>.
     */
    private TokenRule getMatchingRule(String buffer) {
        for (TokenRule rule : this._rules) {
            if (rule.isMatching(buffer)) {
                return rule;
            }
        }

        return null;
    }

    /**
     * Calls the callback with token object if this token is not ignored by the config.
     * @param data Character sequence for the token.
     * @param token The token instance that was matched.
     */
    private void returnToken(CharSequence data, TokenRule token) {
        if (!token.isIgnored()) {
            this._processingCallback.accept(data.toString(), token.getTokenName());
        }
    }
}