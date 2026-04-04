prefix := ""
generated := "generated"
model_file := generated / prefix + "model.json"

gradle := "./gradlew"

# print this help
help:
  @just --list


# build the application, run all the tests
build:
    @{{gradle}} check

# clean up
clean:
    @{{gradle}} clean

# ktlint
lint:
    @ktlint -F src/

# refresh the dependencies
refresh:
    @{{gradle}} refreshVersions       

# run the unit tests
test: clean build

# opens the coverage report in the default browser
coverage-report:
    open build/reports/jacoco/test/html/index.html

# create example model from this project
example-model:
    @{{gradle}} run --quiet --args="parse . src/test/resources/model.json"

# run the application
run *args:
    @{{gradle}} run --quiet --args="{{args}}"

# generate html report for the given model file
html-report MODEL_FILE OUTPUT_FILE="generated/report.html":
    @{{gradle}} run --quiet --args="html-report {{MODEL_FILE}} -o {{OUTPUT_FILE}}"

# run all the tasks for the given REPOSITORY
all-tasks REPOSITORY:
    just run parse {{REPOSITORY}} {{model_file}}

    just run class-statistics {{model_file}} > {{generated}}/{{prefix}}class-statistics.json

    just run file-statistics --include-private-declarations {{model_file}} > {{generated}}/{{prefix}}file-statistics.json

    just run package-coupling {{model_file}} > {{generated}}/{{prefix}}package-coupling.json
    just run package-statistics {{model_file}} > {{generated}}/{{prefix}}package-statistics.json
    just run packages {{model_file}} > {{generated}}/{{prefix}}packages.json

    just run circular-dependencies {{model_file}} > {{generated}}/{{prefix}}circular-dependencies.json

    just run large-classes {{model_file}} > {{generated}}/{{prefix}}large-classes.json
    just run long-methods {{model_file}} > {{generated}}/{{prefix}}long-methods.json
    just run deep-inheritance {{model_file}} > {{generated}}/{{prefix}}deep-inheritance.json
    just run complex-methods {{model_file}} > {{generated}}/{{prefix}}complex-methods.json

    just run search {{model_file}} Declaration > {{generated}}/{{prefix}}search.json

    just run mermaid-class-diagram {{model_file}} > {{generated}}/{{prefix}}mermaid-class-diagram.mermaid
    just run mermaid-import-diagram {{model_file}} >  {{generated}}/{{prefix}}mermaid-import-diagram.mermaid
    just run mermaid-import-diagram {{model_file}} --include-all-libraries > {{generated}}/{{prefix}}mermaid-import-all-diagram.mermaid
    just run mermaid-coupling-diagram {{model_file}} > {{generated}}/{{prefix}}mermaid-coupling-diagram.mermaid
    just run mermaid-coupling-diagram {{model_file}} --include-all-libraries > {{generated}}/{{prefix}}mermaid-coupling-all-diagram.mermaid

    just run html-report {{model_file}} -o {{generated}}/{{prefix}}report.html

# run all-task for this repository
all-tasks-self:
    just all-tasks ./src/main/kotlin/

# check the generated json files
check-jsons:
    jq empty {{generated}}/{{prefix}}inheritance-report.json
    jq empty {{generated}}/{{prefix}}outlier-report.json
    jq empty {{generated}}/{{prefix}}search-report.json
    jq empty {{generated}}/{{prefix}}packages-report.json
    jq empty {{generated}}/{{prefix}}imports-report.json
    jq empty {{generated}}/{{prefix}}imports-all-report.json
    jq empty {{generated}}/{{prefix}}coupling-report.json
    jq empty {{generated}}/{{prefix}}coupling-all-report.json
    jq empty {{generated}}/{{prefix}}file-imports-report.json
    jq empty {{generated}}/{{prefix}}file-imports-all-report.json

    jq empty {{generated}}/{{prefix}}class-statistics.json
    jq empty {{generated}}/{{prefix}}file-statistics.json
    jq empty {{generated}}/{{prefix}}package-statistics.json

# convert a mermaid file (installs mermaid-cli on the fly if needed)
convert-mermaid MERMAID_FILE OUTPUT_FILE:
    @npx -p @mermaid-js/mermaid-cli mmdc -i {{MERMAID_FILE}} -o {{OUTPUT_FILE}}

# show a json file in nu shell
nu-json FILE:
    #!/usr/bin/env nu
    open {{FILE}}
