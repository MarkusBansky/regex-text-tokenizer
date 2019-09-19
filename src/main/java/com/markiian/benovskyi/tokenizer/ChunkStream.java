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

/**
 * Base class for Chunked char data stream. Can stream data in chunks of fixed size and callback each chunk with a fixed step.
 */
abstract class ChunkStream {

    /**
     * Default chunk size.
     */
    @SuppressWarnings("WeakerAccess")
    final Integer _chunkSize = 0x100;

    /**
     * Default processing step size (tiny chunks).
     * The char sequence of this length is sent in callback for processing.
     */
    final Integer _processingStep = 0x20;

    /**
     * Saves previous streamed sequence of chars.
     */
    @SuppressWarnings("unused")
    private CharSequence _previousChunk;

    /**
     * Contains current sequence of chars being processed by stream.
     */
    private CharSequence _currentChunk;

    /**
     * Whole sequence of chars that is going to be processed. Data is copied from {@link String text} passed as a parameter
     * into the openStream method.
     */
    CharSequence _data;

    /**
     * Indicates if the Stream object is in process if streaming data.
     * <strong>True</strong> if the streaming process is in running.
     */
    private Boolean _isStreaming;

    /**
     * The offset of data chars to start processing from. Starts from 0.
     */
    private Integer _dataOffset;

    /**
     * Default callback function that is executed every time when a small chunk message is extracted from the data stream.
     * @param chunk The message to process in a callback.
     */
    protected abstract void processChunk(CharSequence chunk);


    /**
     * Create instance of this class with all values set to defaults for their type.
     */
    ChunkStream() {
        _previousChunk = null;
        _currentChunk = null;

        _dataOffset = 0;
        _data = null;
        _isStreaming = false;
    }

    /**
     * Starts streaming internal text as data.
     * Begins recursive streaming process.
     * @param text The text data of type {@link String} from which stream is created.
     */
    void openStream(String text) {
        _data = text.trim();
        _isStreaming = true;

        nextChunk();
    }

    /**
     * Extract and send for processing next chunk of data.
     */
    private void nextChunk() {
        if (!_isStreaming) return;

        // Get the end of the sub sequence
        Integer newOffset = Math.min(_dataOffset + _chunkSize, _data.length());

        // If the end of subsequence equals to the beginning or is smaller
        // then stop streaming and finish the process.
        if (_dataOffset - newOffset >= 0) {
            _isStreaming = false;
            return;
        }

        // Set previous chunk
        _previousChunk = _currentChunk;
        // Get new chunk
        _currentChunk = _data.subSequence(_dataOffset, newOffset);

        // Set previous chunk offset to new one
        _dataOffset = newOffset;

        // Process chunk
        processChunk(_currentChunk);
        // Recursive call
        nextChunk();
    }

}
