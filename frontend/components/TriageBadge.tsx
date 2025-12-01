import React from 'react';
import { TriageLevel } from '../types';
import { TRIAGE_COLORS, TRIAGE_LABELS } from '../constants';

interface TriageBadgeProps {
  level: TriageLevel;
}

const TriageBadge: React.FC<TriageBadgeProps> = ({ level }) => {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${TRIAGE_COLORS[level]}`}>
      {TRIAGE_LABELS[level]}
    </span>
  );
};

export default TriageBadge;
