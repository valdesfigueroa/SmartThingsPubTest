/**
 *  Text Me When It Opens
 *
 *  Author: SmartThings
 */
definition(
    name: "Text Me When It Opens",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Get a text message sent to your phone when an open/close sensor is opened.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
	section("When the door opens...") {
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Text me at...") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone1", "phone", title: "Phone number?"
        }
	}
}

def installed()
{
	subscribe(contact1, "contact.open", contactOpenHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact.open", contactOpenHandler)
}

def contactOpenHandler(evt) {
	log.trace "$evt.value: $evt, $settings"
	log.debug "$contact1 was opened, texting $phone1"
    if (location.contactBookEnabled) {
        sendNotification("Your ${contact1.label ?: contact1.name} was opened", recipients)
    }
    else {
        sendSms(phone1, "Your ${contact1.label ?: contact1.name} was opened")
    }
}