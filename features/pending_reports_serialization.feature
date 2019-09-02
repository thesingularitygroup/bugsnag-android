Feature: Pending error reports are serialized to disk when an unhandled exception is thrown

Scenario: An unhandled exception is thrown after 10 handled exceptions are reported on a bg thread
    When I run "PendingReportsScenario"
    Then I should receive no requests

    When I configure the app to run in the "online" state
    And I relaunch the app
    Then I should receive 11 requests
# TODO additional validation
