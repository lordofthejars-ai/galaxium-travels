import { useEffect, useState } from 'react';
import { Globe, RotateCcw, Thermometer, Sparkles, Loader2 } from 'lucide-react';
import { Modal } from '../common';
import { getPlanetInfo } from '../../services/api';
import type { PlanetInfo } from '../../types';

interface PlanetInfoModalProps {
  planetName: string | null;
  onClose: () => void;
}

export const PlanetInfoModal = ({ planetName, onClose }: PlanetInfoModalProps) => {
  const [info, setInfo] = useState<PlanetInfo | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!planetName) return;
    setInfo(null);
    setError(null);
    setIsLoading(true);

    getPlanetInfo(planetName)
      .then((data) => setInfo(data))
      .catch(() => setError('Could not load planet information. Please try again.'))
      .finally(() => setIsLoading(false));
  }, [planetName]);

  return (
    <Modal isOpen={!!planetName} onClose={onClose} title={planetName ?? ''} size="sm">
      {isLoading && (
        <div className="flex flex-col items-center justify-center py-10 gap-3">
          <Loader2 className="animate-spin text-cosmic-purple" size={36} />
          <p className="text-star-white/60 text-sm">Scanning galactic database…</p>
        </div>
      )}

      {error && !isLoading && (
        <p className="text-red-400 text-sm text-center py-6">{error}</p>
      )}

      {info && !isLoading && (
        <div className="space-y-5">
          {/* Stats grid */}
          <div className="grid grid-cols-3 gap-3">
            <StatCard
              icon={<Globe size={18} className="text-cosmic-purple" />}
              label="Orbital Period"
              value={`${info.orbitalPeriod} days`}
            />
            <StatCard
              icon={<RotateCcw size={18} className="text-nebula-pink" />}
              label="Rotation Period"
              value={`${info.rotationPeriod} days`}
            />
            <StatCard
              icon={<Thermometer size={18} className="text-solar-orange" />}
              label="Avg Temperature"
              value={`${info.averageSurfaceTemperature}°C`}
            />
          </div>

          {/* Interesting facts */}
          {info.interestingFacts.length > 0 && (
            <div>
              <div className="flex items-center gap-2 mb-3">
                <Sparkles size={16} className="text-alien-green" />
                <h3 className="text-sm font-semibold text-star-white/80 uppercase tracking-wider">
                  Interesting Facts
                </h3>
              </div>
              <ul className="space-y-2">
                {info.interestingFacts.map((fact, i) => (
                  <li
                    key={i}
                    className="flex gap-2 text-sm text-star-white/80 leading-relaxed"
                  >
                    <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-cosmic-purple" />
                    {fact}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </Modal>
  );
};

interface StatCardProps {
  icon: React.ReactNode;
  label: string;
  value: string;
}

const StatCard = ({ icon, label, value }: StatCardProps) => (
  <div className="flex flex-col items-center gap-1 rounded-xl bg-white/5 border border-white/10 p-3 text-center">
    {icon}
    <p className="text-xs text-star-white/50 mt-1">{label}</p>
    <p className="text-sm font-semibold text-star-white">{value}</p>
  </div>
);

// Made with Bob
