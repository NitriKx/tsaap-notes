/*
 * Copyright 2013 Tsaap Development Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tsaap.questions;

import java.util.List;

/**
 * @author franck Silvestre
 */
public interface Question {

    /**
     * Get the title of the question
     *
     * @return the title of the question
     */
    public String getTitle();

    /**
     * Get the question block list
     *
     * @return the question block list
     */
    public List<QuestionBlock> getBlockList();

    /**
     * Get the question text block list
     *
     * @return the text question block list
     */
    public List<TextBlock> getTextBlockList();

    /**
     * Get the question text block list
     *
     * @return the text question block list
     */
    public List<AnswerBlock> getAnswerBlockList();

    /**
     * Get the question type
     *
     * @return the question type
     */
    public QuestionType getQuestionType();

    /**
     * Get the general feedback for the question
     * @return the general feedback for the question
     */
    public String getGeneralFeedback();

}
