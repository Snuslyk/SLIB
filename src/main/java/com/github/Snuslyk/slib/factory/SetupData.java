package com.github.Snuslyk.slib.factory;

import com.github.Snuslyk.slib.Controller;

public record SetupData(Controller controller, int sectionIndex, int objectIndex, int optionIndex, Form form) {
}
