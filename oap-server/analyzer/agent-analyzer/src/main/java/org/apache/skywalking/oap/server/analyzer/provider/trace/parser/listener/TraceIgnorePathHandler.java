/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.analyzer.provider.trace.parser.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.util.StringUtil;
import org.apache.skywalking.apm.util.matcher.FastPathMatcher;
import org.apache.skywalking.apm.util.matcher.TracePathMatcher;
import org.apache.skywalking.oap.server.analyzer.provider.trace.TraceIgnorePathWatcher;

/**
 * The sampler makes the sampling mechanism works at backend side. Sample result: [0,sampleRate) sampled, (sampleRate,~)
 * ignored
 */
@Slf4j
public class TraceIgnorePathHandler {
    private TraceIgnorePathWatcher traceIgnorePathWatcher;
    private static final String PATTERN_SEPARATOR = ",";
    private TracePathMatcher pathMatcher = new FastPathMatcher();
    private String[] patterns = new String[] {};

    public TraceIgnorePathHandler(TraceIgnorePathWatcher traceIgnorePathWatcher) {
        this.traceIgnorePathWatcher = traceIgnorePathWatcher;
        if (StringUtil.isNotBlank(traceIgnorePathWatcher.getTraceIgnorePathPatterns())) {
            patterns = traceIgnorePathWatcher.getTraceIgnorePathPatterns().split(PATTERN_SEPARATOR);
        }
    }

    public boolean shouldIgnore(String operationName) {
        if (StringUtil.isNotBlank(traceIgnorePathWatcher.getTraceIgnorePathPatterns())
            && traceIgnorePathWatcher.getTraceIgnorePathPatterns().split(PATTERN_SEPARATOR).length > 0) {
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, operationName)) {
                    log.debug("operationName : {} has been Ignored", operationName);
                    return true;
                }
            }
        }

        return false;
    }
}
