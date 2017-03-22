#!/bin/sh
# === OpenMRS let's rethink our unit test style ===
# Removes every line with @should, @Verifies, @verifies
# Removes import of org.openmrs.test.Verifies

find . -name "*.java" \
    -exec sed -i "" "/@Verifies*/d" {} \;
find . -name "*.java" \
    -exec sed -i "" "/@verifies*/d" {} \;
find . -name "*.java" \
    -exec sed -i "" "/@should*/d" {} \;
find . -name "*.java" \
    -exec sed -i "" "/import org.openmrs.test.Verifies;/d" {} \;

