/**
 *  Medicine Reminder
 *
 *  Author: SmartThings
 */

preferences {
	section("Choose your medicine cabinet..."){
		input "cabinet1", "capability.contactSensor", title: "Where?"
	}
	section("Take my medicine at..."){
		input "time1", "time", title: "Time 1"
		input "time2", "time", title: "Time 2", required: false
		input "time3", "time", title: "Time 3", required: false
		input "time4", "time", title: "Time 4", required: false
	}
	section("I forget send me a notification and/or text message..."){
		input "sendPush", "enum", title: "Push Notifiation", required: false, metadata: [values: ["Yes","No"]]
		input "phone1", "phone", title: "Phone Number", required: false
	}
	section("Time window (optional, defaults to plus or minus 15 minutes") {
		input "timeWindow", "decimal", title: "Minutes", required: false
	}
}

def installed()
{
	initialize()
}

def updated()
{
	unschedule()
	initialize()
}

def initialize() {
	def window = timeWindowMsec
	[time1, time2, time3, time4].eachWithIndex {time, index ->
		if (time != null) {
			def endTime = new Date(timeToday(time).time + window)
			log.debug "Scheduling check at $endTime"
			//runDaily(endTime, "scheduleCheck${index}")
			switch (index) {
				case 0:
					runDaily(endTime, scheduleCheck0)
					break
				case 1:
					runDaily(endTime, scheduleCheck1)
					break
				case 2:
					runDaily(endTime, scheduleCheck2)
					break
				case 3:
					runDaily(endTime, scheduleCheck3)
					break
			}
		}
	}
}

def scheduleCheck0() { scheduleCheck() }
def scheduleCheck1() { scheduleCheck() }
def scheduleCheck2() { scheduleCheck() }
def scheduleCheck3() { scheduleCheck() }

def scheduleCheck()
{
	log.debug "scheduleCheck"
	def t0 = new Date(now() - (2 * timeWindowMsec))
	def t1 = new Date()
	def cabinetOpened = cabinet1.eventsBetween(t0, t1).find{it.name = "contact" && it.value == "open"}
	log.trace "Looking for events between $t0 and $t1: $cabinetOpened"

	if (cabinetOpened) {
		log.trace "Medicine cabinet was opened since $midnight, no notification required"
	} else {
		log.trace "Medicine cabinet was not opened since $midnight, sending notification"
		sendMessage()
	}
}

private sendMessage() {
	def msg = "Please remember to take your medicine"
	log.info msg
	if (phone1) {
		sendSms(phone1, msg)
	}
	if (sendPush == "Yes") {
		sendPush(msg)
	}
}

def getTimeWindowMsec() {
	(timeWindow ?: 15) * 60000 as Long
}
