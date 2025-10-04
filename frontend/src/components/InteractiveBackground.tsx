import React, { useRef, useEffect, useCallback } from 'react';

const PARTICLE_DISTANCE = 35;
const PARTICLE_SIZE = 1.35;
const PARTICLE_COLOR = 'rgba(255, 255, 255, 0.5)';

const SPRING_STRENGTH = 0.005;
const DAMPING = 0.94;

const INTERACTION_RADIUS = 250;
const MAX_MOUSE_REPEL_STRENGTH = 2;

const MIN_SPEED_FOR_EFFECT = 0.5;
const MAX_SPEED_FOR_FULL_EFFECT = 30;
const MOUSE_SPEED_DECAY = 0.97;

interface Particle {
  x: number; y: number; originalX: number; originalY: number; vx: number; vy: number;
}

const InteractiveBackground: React.FC = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const particlesRef = useRef<Particle[]>([]);
  const mousePosRef = useRef({ x: -1000, y: -1000 });
  const mouseSpeedRef = useRef(0);
  const animationFrameId = useRef<number>();

  const animate = useCallback(() => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = PARTICLE_COLOR;

    const speedRange = MAX_SPEED_FOR_FULL_EFFECT - MIN_SPEED_FOR_EFFECT;
    const speedProgress = (mouseSpeedRef.current - MIN_SPEED_FOR_EFFECT) / speedRange;
    const strengthMultiplier = Math.max(0, Math.min(1, speedProgress));
    const currentRepelStrength = MAX_MOUSE_REPEL_STRENGTH * strengthMultiplier;

    particlesRef.current.forEach(p => {
      p.vx += (p.originalX - p.x) * SPRING_STRENGTH;
      p.vy += (p.originalY - p.y) * SPRING_STRENGTH;

      if (currentRepelStrength > 0) {
        const dx = p.x - mousePosRef.current.x;
        const dy = p.y - mousePosRef.current.y;
        const dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < INTERACTION_RADIUS) {
          const force = (1 - dist / INTERACTION_RADIUS) * currentRepelStrength;
          p.vx += (dx / dist) * force;
          p.vy += (dy / dist) * force;
        }
      }

      p.vx *= DAMPING;
      p.vy *= DAMPING;
      p.x += p.vx;
      p.y += p.vy;

      ctx.beginPath();
      ctx.arc(p.x, p.y, PARTICLE_SIZE, 0, Math.PI * 2);
      ctx.fill();
    });


    mouseSpeedRef.current *= MOUSE_SPEED_DECAY;

    animationFrameId.current = requestAnimationFrame(animate);
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    let lastMousePos = { x: 0, y: 0 };

    const initialize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
      particlesRef.current = [];
      const cols = Math.floor(canvas.width / PARTICLE_DISTANCE);
      const rows = Math.floor(canvas.height / PARTICLE_DISTANCE);
      const offsetX = (canvas.width - cols * PARTICLE_DISTANCE) / 2;
      const offsetY = (canvas.height - rows * PARTICLE_DISTANCE) / 2;

      for (let r = 0; r < rows; r++) {
        for (let c = 0; c < cols; c++) {
          const x = c * PARTICLE_DISTANCE + offsetX;
          const y = r * PARTICLE_DISTANCE + offsetY;
          particlesRef.current.push({ x, y, originalX: x, originalY: y, vx: 0, vy: 0 });
        }
      }
      lastMousePos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };
    };

    const handleMouseMove = (e: MouseEvent) => {
      const rect = canvas.getBoundingClientRect();
      const newMousePos = { x: e.clientX - rect.left, y: e.clientY - rect.top };
      mousePosRef.current = newMousePos;
      const vx = newMousePos.x - lastMousePos.x;
      const vy = newMousePos.y - lastMousePos.y;

      mouseSpeedRef.current = Math.sqrt(vx * vx + vy * vy);
      lastMousePos = newMousePos;
    };
    const handleMouseLeave = () => {
      mousePosRef.current = { x: -1000, y: -1000 };
      mouseSpeedRef.current = 0;
    };

    initialize();
    window.addEventListener('resize', initialize);
    window.addEventListener('mousemove', handleMouseMove);
    document.body.addEventListener('mouseleave', handleMouseLeave);
    animationFrameId.current = requestAnimationFrame(animate);

    return () => {
      if (animationFrameId.current) cancelAnimationFrame(animationFrameId.current);
      window.removeEventListener('resize', initialize);
      window.removeEventListener('mousemove', handleMouseMove);
      document.body.removeEventListener('mouseleave', handleMouseLeave);
    };
  }, [animate]);

  return (
    <div className="fixed inset-0 z-0 bg-[#0B0B0B]">
      <canvas ref={canvasRef}></canvas>
      <div className="absolute inset-x-0 top-0 h-4/5 pointer-events-none 
                        bg-gradient-to-b from-[#0B0B0B] via-[#0B0B0B]/80 to-transparent">
      </div>
    </div>
  );
};

export default InteractiveBackground;
