/**
 *  Scheduled Mode Change - Presence Optional
 *
 *  Author: SmartThings

 *
 */

definition(
    name: "Scheduled Mode Change",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Changes mode at a specific time of day.",
    category: "Mode Magic",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-LightUpMyWorld.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-LightUpMyWorld@2x.png"
)

preferences {
	section("At this time every day") {
		input "time", "time", title: "Time of Day"
	}
	section("Change to this mode") {
		input "newMode", "mode", title: "Mode?"
	}
	section( "Notifications" ) {
        input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phoneNumber", "phone", title: "Send a text message?", required: false
        }
	}
}

def installed() {
	initialize()
}

def updated() {
	unschedule()
	initialize()
}

def initialize() {
	schedule(time, changeMode)
}

def changeMode() {
	log.debug "changeMode, location.mode = $location.mode, newMode = $newMode, location.modes = $location.modes"
	if (location.mode != newMode) {
		if (location.modes?.find{it.name == newMode}) {
			setLocationMode(newMode)
			send "${label} has changed the mode to '${newMode}'"
		}
		else {
			send "${label} tried to change to undefined mode '${newMode}'"
		}
	}
}

private send(msg) {

    if (location.contactBookEnabled) {
        log.debug("sending notifications to: ${recipients?.size()}")
        sendNotificationToContacts(msg, recipients)
    }
    else {
        if (sendPushMessage == "Yes") {
            log.debug("sending push message")
            sendPush(msg)
        }

        if (phoneNumber) {
            log.debug("sending text message")
            sendSms(phoneNumber, msg)
        }
    }

	log.debug msg
}

private getLabel() {
	app.label ?: "SmartThings"
}