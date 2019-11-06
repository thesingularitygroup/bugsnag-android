Feature: Reporting handled Exceptions

Scenario: Test handled Kotlin Exception
    Given I configure the app to run in the "LoadConfiguration" state
    When I run "LoadConfigurationScenario"
    Then I wait to receive 2 requests
    And the request is valid for the session reporting API version "1.0" for the "Android Bugsnag Notifier" notifier
    And I discard the oldest request
    And the request is valid for the error reporting API version "4.0" for the "Android Bugsnag Notifier" notifier
    And the payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LoadConfigException"
    And the event "metaData.test.redacted" equals "[REDACTED]"
    And the event "metaData.test.present" equals "bar"
    And the event "app.version" equals "9.8.7"
